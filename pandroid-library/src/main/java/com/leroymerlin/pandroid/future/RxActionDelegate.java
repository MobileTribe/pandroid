package com.leroymerlin.pandroid.future;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;

/**
 * Created by florian on 07/06/2017.
 */

public class RxActionDelegate<T> extends CancellableActionDelegate<T> {

    private ActionDelegate<T> wrapDelegate;

    private RxActionDelegate() {
    }

    public static <T> Single<T> single(final OnSubscribeAction<T> subscribe) {
        final RxActionDelegate<T> delegate = new RxActionDelegate<>();
        return Single.<T>create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull final SingleEmitter<T> emitter) throws Exception {
                delegate.setDelegate(new ActionDelegate<T>() {
                    @Override
                    public void onSuccess(T result) {
                        emitter.onSuccess(result);
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.onError(e);
                    }
                });
                subscribe.subscribe(delegate);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                delegate.cancel();
            }
        });
    }

    public static <T> Single<Result<T>> singleWrapped(final OnSubscribeAction<T> subscribe) {
        final RxActionDelegate<T> delegate = new RxActionDelegate<>();
        return Single.<Result<T>>create(new SingleOnSubscribe<Result<T>>() {
            @Override
            public void subscribe(@NonNull final SingleEmitter<Result<T>> emitter) throws Exception {
                delegate.setDelegate(new ActionDelegate<T>() {
                    @Override
                    public void onSuccess(T result) {
                        emitter.onSuccess(new Result<T>(result));
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.onSuccess(new Result<T>(e));
                    }
                });
                subscribe.subscribe(delegate);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                delegate.cancel();
            }
        });
    }

    public static <T> Result<T> wrap(T value) {
        return new Result<T>(value);
    }

    public static <T> Observable<T> observable(final OnSubscribeAction<T> subscribe) {
        final RxActionDelegate<T> delegate = new RxActionDelegate<>();
        return Observable.<T>create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<T> emitter) throws Exception {
                delegate.setDelegate(new ActionDelegate<T>() {
                    @Override
                    public void onSuccess(T result) {
                        emitter.onNext(result);
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.onError(e);
                    }
                });
                subscribe.subscribe(delegate);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                delegate.cancel();
            }
        });
    }

    public static <T> Observable<Result<T>> observableWrapped(final OnSubscribeAction<T> subscribe) {
        final RxActionDelegate<T> delegate = new RxActionDelegate<>();
        return Observable.<Result<T>>create(new ObservableOnSubscribe<Result<T>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Result<T>> emitter) throws Exception {
                delegate.setDelegate(new ActionDelegate<T>() {
                    @Override
                    public void onSuccess(T result) {
                        emitter.onNext(new Result<T>(result));
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.onNext(new Result<T>(e));
                    }
                });
                subscribe.subscribe(delegate);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                delegate.cancel();
            }
        });
    }


    private void setDelegate(ActionDelegate<T> delegate) {
        this.wrapDelegate = delegate;
    }

    @Override
    protected void success(T result) {
        if (this.wrapDelegate != null) {
            this.wrapDelegate.onSuccess(result);
        }
    }

    @Override
    protected void error(Exception e) {
        if (this.wrapDelegate != null) {
            this.wrapDelegate.onError(e);
        }
    }



    public static class Result<T> {
        public T result;
        public Exception error;

        Result(T result) {
            this.result = result;
        }

        Result(Exception error) {
            this.error = error;
        }

        public boolean hasResult() {
            return result != null;
        }

    }

    public interface OnSubscribeAction<T> {
        void subscribe(ActionDelegate<T> delegate);
    }
}
