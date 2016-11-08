package com.leroymerlin.pandroid.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.leroymerlin.pandroid.future.ActionDelegate;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.net.http.Mock;
import com.leroymerlin.pandroid.net.mock.ServiceMock;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
    Handler handler;
    private boolean mockEnable;
    private PandroidErrorFormatter errorFormatter;

    public static PandroidCallAdapterFactory create(Context context, LogWrapper logWrapper) {
        return new PandroidCallAdapterFactory(context, logWrapper);
    }

    private PandroidCallAdapterFactory(Context context, LogWrapper logWrapper) {
        handler = new Handler(Looper.getMainLooper());
        this.context = context;
        this.logWrapper = logWrapper;
    }

    public void setMockEnable(boolean mockEnable) {
        this.mockEnable = mockEnable;
    }

    public void setErrorFormatter(PandroidErrorFormatter errorFormatter) {
        this.errorFormatter = errorFormatter;
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (returnType instanceof ParameterizedType && (((ParameterizedType) returnType).getRawType() == PandroidCall.class || ((ParameterizedType) returnType).getRawType() == Call.class))
            return new ResponseCallAdapter(((ParameterizedType) returnType).getActualTypeArguments()[0], annotations);
        return null;
    }

    final class ResponseCallAdapter implements CallAdapter<PandroidCall<?>> {
        private final Type responseType;
        private ServiceMock serviceMock;

        ResponseCallAdapter(Type responseType, Annotation[] annotations) {
            this.responseType = responseType;

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
        public <R> PandroidCall<?> adapt(final Call<R> call) {
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
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(call, (Response<R>) serviceMock.getMockResponse(call.request(), context));
                            }
                        }, serviceMock.getResponseDelay());
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
            return new PandroidCallImpl<R>(callWrapper);
        }
    }

    private class PandroidCallImpl<R> implements PandroidCall<R> {
        private final Call<R> call;

        PandroidCallImpl(Call<R> call) {
            this.call = call;
        }

        @Override
        public void enqueue(final ActionDelegate delegate) {
            if (delegate instanceof CancellableActionDelegate)
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
                            if (response.isSuccessful()) {
                                handler.post(
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
                                onError(new NetworkException(response.raw().request().url().toString(), response.code(), response.headers().toMultimap(), new Exception(response.message()), bytes, System.currentTimeMillis() - startTime));
                            }
                        }

                        @Override
                        public void onFailure(Call<R> call, final Throwable t) {
                            onError(new Exception(t));
                        }

                        private void onError(final Exception e) {
                            handler.post(new Runnable() {
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
            return new PandroidCallImpl<R>(call.clone());
        }

        @Override
        public Request request() {
            return call.request();
        }
    }
}