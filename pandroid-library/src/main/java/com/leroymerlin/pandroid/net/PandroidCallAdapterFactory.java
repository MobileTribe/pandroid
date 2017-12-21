package com.leroymerlin.pandroid.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.leroymerlin.pandroid.app.PandroidConfig;
import com.leroymerlin.pandroid.future.ActionDelegate;
import com.leroymerlin.pandroid.future.Cancellable;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;
import com.leroymerlin.pandroid.future.RxActionDelegate;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.net.http.Mock;
import com.leroymerlin.pandroid.net.mock.ServiceMock;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.TreeMap;

import io.reactivex.Single;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by florian on 08/01/16.
 */
public final class PandroidCallAdapterFactory extends CallAdapter.Factory {

    private static final String TAG = "PandroidCallAdapterFactory";
    private final Context context;
    private final LogWrapper logWrapper;
    private final boolean rxAndroidEnable;
    Handler handler;
    private boolean mockEnable;
    private PandroidErrorFormatter errorFormatter;

    public static PandroidCallAdapterFactory create(Context context, LogWrapper logWrapper) {
        return PandroidCallAdapterFactory.create(context, logWrapper, Looper.getMainLooper());
    }

    public static PandroidCallAdapterFactory create(Context context, LogWrapper logWrapper, @Nullable Looper looper) {
        return new PandroidCallAdapterFactory(context, logWrapper, looper);
    }

    private PandroidCallAdapterFactory(Context context, LogWrapper logWrapper, Looper looper) {
        if (looper != null)
            handler = new Handler(looper);
        this.context = context;
        this.logWrapper = logWrapper;
        rxAndroidEnable = PandroidConfig.isLibraryEnable("rxandroid");
    }

    public void setMockEnable(boolean mockEnable) {
        this.mockEnable = mockEnable;
    }

    public void setErrorFormatter(PandroidErrorFormatter errorFormatter) {
        this.errorFormatter = errorFormatter;
    }

    @Override
    public CallAdapter<?, PandroidCall> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (returnType instanceof ParameterizedType && (((ParameterizedType) returnType).getRawType() == PandroidCall.class || ((ParameterizedType) returnType).getRawType() == RxPandroidCall.class || ((ParameterizedType) returnType).getRawType() == Call.class)) {
            Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
            return new ResponseCallAdapter(actualTypeArguments[0], annotations);
        }
        return null;
    }

    final class ResponseCallAdapter<R> implements CallAdapter<R, PandroidCall<?>> {
        private boolean needResponse;
        private Type responseType;
        private ServiceMock serviceMock;

        ResponseCallAdapter(Type responseType, Annotation[] annotations) {
            this.responseType = responseType;
            if (responseType instanceof ParameterizedType && (((ParameterizedType) responseType).getRawType()).equals(Response.class)) {
                Type[] actualTypeArguments = ((ParameterizedType) responseType).getActualTypeArguments();
                this.responseType = actualTypeArguments[0];
                this.needResponse = true;
            }
            for (Annotation annotation : annotations) {
                if (annotation instanceof Mock) {
                    try {
                        serviceMock = ((Mock) annotation).mockClass().newInstance();
                        serviceMock.setMockInfo((Mock) annotation);
                        serviceMock.setReturnType(responseType);
                    } catch (Exception e) {
                        logWrapper.e(TAG, e);
                    }
                }
            }
        }

        @Override
        public Type responseType() {
            return responseType;
        }


        @Override
        public PandroidCall<?> adapt(final Call<R> call) {
            Call<R> callWrapper = call;
            if (serviceMock != null && serviceMock.isEnable() && mockEnable) {
                callWrapper = new Call<R>() {
                    public boolean isExecuted;

                    @Override
                    public Response<R> execute() {
                        isExecuted = true;
                        try {
                            Thread.sleep(serviceMock.getResponseDelay());
                        } catch (InterruptedException e) {
                            logWrapper.e(TAG, e);
                        }
                        isExecuted = false;
                        return serviceMock.getMockResponse(call.request(), context);
                    }

                    @Override
                    public void enqueue(final Callback<R> callback) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(call, (Response<R>) serviceMock.getMockResponse(call.request(), context));
                            }
                        };
                        if (handler != null) {
                            handler.postDelayed(runnable, serviceMock.getResponseDelay());
                        } else {
                            runnable.run();
                        }
                    }

                    @Override
                    public boolean isExecuted() {
                        return isExecuted;
                    }

                    @Override
                    public void cancel() {
                        call.cancel();
                    }

                    @Override
                    public boolean isCanceled() {
                        return call.isCanceled();
                    }

                    @Override
                    public Call<R> clone() {
                        return call.clone();
                    }

                    @Override
                    public Request request() {
                        return call.request();
                    }
                };
            }
            return rxAndroidEnable ? new RxPandroidCallImpl<>(callWrapper, needResponse) : new PandroidCallImpl<>(callWrapper, needResponse);
        }
    }

    private void post(Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    protected class PandroidCallImpl<R> implements PandroidCall<R> {
        protected final Call<R> call;
        protected final boolean needResponse;

        PandroidCallImpl(Call<R> call, boolean needResponse) {
            this.call = call;
            this.needResponse = needResponse;
        }

        @Override
        public void enqueue(final ActionDelegate delegate) {
            if (delegate instanceof Cancellable)
                ((CancellableActionDelegate) delegate).addCancelListener(new CancellableActionDelegate.CancelListener() {
                    @Override
                    public void onCancel() {
                        call.cancel();
                    }
                });
            final long startTime = System.currentTimeMillis();

            call.enqueue(
                    new Callback<R>() {


                        @Override
                        public void onResponse(Call<R> call, final Response<R> response) {
                            if (needResponse) {
                                post(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                delegate.onSuccess(response);
                                            }
                                        }
                                );
                            } else {
                                if (response.isSuccessful()) {
                                    post(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    delegate.onSuccess(response.body());
                                                }
                                            }
                                    );
                                } else {
                                    byte[] bytes = new byte[0];

                                    try {
                                        bytes = response.errorBody().bytes();
                                    } catch (IOException ignore) {
                                    }
                                    onError(new NetworkException(response.raw().request().url().toString(), response.code(), (TreeMap) response.headers().toMultimap(), new Exception(response.message()), bytes, System.currentTimeMillis() - startTime));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<R> call, final Throwable t) {

                            onError(new Exception(t));
                        }

                        private void onError(final Exception e) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    delegate.onError(errorFormatter != null ? errorFormatter.format(e) : e);
                                }
                            });
                        }
                    }

            );
        }


        @Override
        public Response<R> execute() throws IOException {
            return call.execute();
        }

        @Override
        public void enqueue(Callback<R> callback) {
            call.enqueue(callback);
        }

        @Override
        public boolean isExecuted() {
            return call.isExecuted();
        }

        @Override
        public void cancel() {
            call.cancel();
        }

        @Override
        public boolean isCanceled() {
            return call.isCanceled();
        }

        @Override
        public PandroidCall<R> clone() {
            return new PandroidCallImpl<>(call.clone(), needResponse);
        }

        @Override
        public Request request() {
            return call.request();
        }
    }

    public class RxPandroidCallImpl<T> extends PandroidCallAdapterFactory.PandroidCallImpl<T> implements RxPandroidCall<T> {

        RxPandroidCallImpl(Call<T> call, boolean needResponse) {
            super(call, needResponse);
        }

        public Single<T> rxEnqueue() {
            return RxActionDelegate.single(new RxActionDelegate.OnSubscribeAction<T>() {
                @Override
                public void subscribe(ActionDelegate<T> callback) {
                    RxPandroidCallImpl.this.clone().enqueue(callback);
                }
            });
        }

        @Override
        public RxPandroidCall<T> clone() {
            return new RxPandroidCallImpl(call.clone(), needResponse);
        }
    }

}