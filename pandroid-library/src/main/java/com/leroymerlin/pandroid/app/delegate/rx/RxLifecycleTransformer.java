package com.leroymerlin.pandroid.app.delegate.rx;

import org.reactivestreams.Publisher;

import java.util.concurrent.CancellationException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RxLifecycleTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T,
                T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    private Observable<?> mObservable;

    RxLifecycleTransformer(Observable<?> observable) {
        mObservable = observable;
    }

    /**
     * TakeUntil n'existe pas sur un completable. Amb permet de la premère émission
     *
     * @param upstream
     * @return
     */
    @Override
    public CompletableSource apply(Completable upstream) {
        return Completable.ambArray(upstream, mObservable.flatMapCompletable(new Function<Object, CompletableSource>() {
            @Override
            public CompletableSource apply(@NonNull Object o) throws Exception {
                return Completable.error(new CancellationException());
            }
        }));
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.takeUntil(mObservable.toFlowable(BackpressureStrategy.LATEST));
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.takeUntil(mObservable.firstElement());
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(mObservable);
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(mObservable.firstOrError());
    }

}