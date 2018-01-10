package com.leroymerlin.pandroid.app.delegate.rx;

import android.support.annotation.Nullable;

import com.leroymerlin.pandroid.app.delegate.PandroidDelegateProvider;

import org.reactivestreams.Publisher;

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
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainObserverTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    private PandroidDelegateProvider provider;

    MainObserverTransformer(@Nullable PandroidDelegateProvider pandroidDelegateProvider) {
        this.provider = pandroidDelegateProvider;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        Completable tObservable = upstream
                .observeOn(AndroidSchedulers.mainThread());
        if (provider == null) {
            return tObservable;
        }
        return tObservable.compose(RxLifecycleDelegate.<T>bindLifecycle(provider));
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        Flowable<T> tObservable = upstream
                .observeOn(AndroidSchedulers.mainThread());
        if (provider == null) {
            return tObservable;
        }
        return tObservable.compose(RxLifecycleDelegate.<T>bindLifecycle(provider));
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        Maybe<T> tObservable = upstream
                .observeOn(AndroidSchedulers.mainThread());
        if (provider == null) {
            return tObservable;
        }
        return tObservable.compose(RxLifecycleDelegate.<T>bindLifecycle(provider));
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        Observable<T> tObservable = upstream
                .observeOn(AndroidSchedulers.mainThread());
        if (provider == null) {
            return tObservable;
        }
        return tObservable.compose(RxLifecycleDelegate.<T>bindLifecycle(provider));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        Single<T> tObservable = upstream
                .observeOn(AndroidSchedulers.mainThread());
        if (provider == null) {
            return tObservable;
        }
        return tObservable.compose(RxLifecycleDelegate.<T>bindLifecycle(provider));
    }
}