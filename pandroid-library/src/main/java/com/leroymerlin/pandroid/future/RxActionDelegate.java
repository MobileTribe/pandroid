package com.leroymerlin.pandroid.future;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * Created by florian on 07/06/2017.
 */

public class RxActionDelegate<T> extends CancellableActionDelegate<T> implements Disposable {

    private ActionDelegate<T> wrapDelegate;

    private RxActionDelegate(ActionDelegate<T> delegate) {
        this.wrapDelegate = delegate;
    }

    public static <T> Single<T> single(final OnSubscribeAction<T> subscribe) {
        return Single.<T>create(emitter -> {
            RxActionDelegate<T> delegate = new RxActionDelegate<>(new ActionDelegate<T>() {
                @Override
                public void onSuccess(T result) {
                    emitter.onSuccess(result);
                }

                @Override
                public void onError(Exception e) {
                    emitter.onError(e);
                }
            });
            emitter.setDisposable(delegate);
            subscribe.subscribe(delegate);
        });
    }

    public static <T> Single<Result<T>> singleWrapped(final OnSubscribeAction<T> subscribe) {
        return Single.<Result<T>>create(emitter -> {
            final RxActionDelegate<T> delegate = new RxActionDelegate<>(new ActionDelegate<T>() {
                @Override
                public void onSuccess(T result) {
                    emitter.onSuccess(new Result<T>(result));
                }

                @Override
                public void onError(Exception e) {
                    emitter.onSuccess(new Result<T>(e));
                }
            });
            emitter.setDisposable(delegate);
            subscribe.subscribe(delegate);
        });
    }


    public static <T> Observable<T> observable(final OnSubscribeAction<T> subscribe) {
        return Observable.<T>create(emitter -> {
            final RxActionDelegate<T> delegate = new RxActionDelegate<>(new ActionDelegate<T>() {
                @Override
                public void onSuccess(T result) {
                    emitter.onNext(result);
                }

                @Override
                public void onError(Exception e) {
                    emitter.onError(e);
                }
            });
            emitter.setDisposable(delegate);
            subscribe.subscribe(delegate);
        });
    }

    public static <T> Observable<Result<T>> observableWrapped(final OnSubscribeAction<T> subscribe) {
        return Observable.<Result<T>>create(emitter -> {
            final RxActionDelegate<T> delegate = new RxActionDelegate<>(new ActionDelegate<T>() {
                @Override
                public void onSuccess(T result) {
                    emitter.onNext(new Result<T>(result));
                }

                @Override
                public void onError(Exception e) {
                    emitter.onNext(new Result<T>(e));
                }
            });
            emitter.setDisposable(delegate);
            subscribe.subscribe(delegate);
        });
    }

    public static <T> Result<T> wrap(T value) {
        return new Result<T>(value);
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

    @Override
    public void dispose() {
        cancel();
    }

    @Override
    public boolean isDisposed() {
        return isCancelled();
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
