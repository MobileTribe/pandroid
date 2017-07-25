package com.leroymerlin.pandroid.app.delegate.rx;

import org.reactivestreams.Publisher;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;

final class RxLifecycleTransformerEmpty<T> extends RxLifecycleTransformer<T> {

    RxLifecycleTransformerEmpty() {
        super(null);
    }

    /**
     * TakeUntil n'existe pas sur un completable. Amb permet de la premère émission
     * @param upstream
     * @return
     */
    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream;
    }
    
    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream;
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream;
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream;
    }


}