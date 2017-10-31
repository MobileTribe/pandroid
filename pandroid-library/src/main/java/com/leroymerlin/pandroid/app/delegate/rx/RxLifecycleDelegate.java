package com.leroymerlin.pandroid.app.delegate.rx;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.annotations.RxModel;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegateProvider;
import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Mehdi on 01/06/2017.
 */
public class RxLifecycleDelegate extends SimpleLifecycleDelegate {

    private final BehaviorSubject<RxLifeCycleEvent> mLifecycleSubject = BehaviorSubject.create();


    @RxModel(targets = {PandroidDelegateProvider.class})
    public static <T> RxLifecycleTransformer<T> bindLifecycle(PandroidDelegateProvider provider, final RxLifeCycleEvent event) {
        PandroidDelegate pandroidDelegate = provider == null ? null : provider.getPandroidDelegate();
        if (pandroidDelegate == null) {
            return new RxLifecycleTransformerEmpty<>();
        }

        RxLifecycleDelegate delegate = (RxLifecycleDelegate) pandroidDelegate.getLifecycleDelegate(RxLifecycleDelegate.class);
        return delegate.bind(delegate.mLifecycleSubject.filter(new Predicate<RxLifeCycleEvent>() {
            @Override
            public boolean test(@NonNull RxLifeCycleEvent lifecycleEvent) throws Exception {
                return lifecycleEvent.equals(event);
            }
        }));
    }

    @RxModel(targets = {PandroidDelegateProvider.class})
    public static <T> RxLifecycleTransformer<T> bindLifecycle(PandroidDelegateProvider provider) {
        PandroidDelegate pandroidDelegate = provider == null ? null : provider.getPandroidDelegate();
        if (pandroidDelegate == null) {
            return new RxLifecycleTransformerEmpty<>();
        }
        RxLifecycleDelegate delegate = (RxLifecycleDelegate) pandroidDelegate.getLifecycleDelegate(RxLifecycleDelegate.class);
        Observable<RxLifeCycleEvent> lifecycleObservable = delegate.mLifecycleSubject.share();

        return delegate.bind(
                Observable.combineLatest(
                        // récupération du premier et donner son équivalent pour quitter
                        lifecycleObservable.take(1).map(new Function<RxLifeCycleEvent, RxLifeCycleEvent>() {
                            @Override
                            public RxLifeCycleEvent apply(@NonNull RxLifeCycleEvent lifecycleEvent) throws Exception {
                                switch (lifecycleEvent) {
                                    case ON_CREATE:
                                        return RxLifeCycleEvent.ON_DESTROY;
                                    case ON_RESUME:
                                        return RxLifeCycleEvent.ON_PAUSE;
                                    default:
                                        throw new UnsupportedOperationException("Binding to " + lifecycleEvent + " not " +
                                                "yet implemented");
                                }
                            }
                        }),
                        // récupération du suivant
                        lifecycleObservable.skip(1),
                        new BiFunction<RxLifeCycleEvent, RxLifeCycleEvent, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull RxLifeCycleEvent rxLifeCycleEvent, @NonNull RxLifeCycleEvent rxLifeCycleEvent2) throws Exception {
                                return rxLifeCycleEvent.equals(rxLifeCycleEvent2);
                            }
                        }
                ).onErrorReturn(new Function<Throwable, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Throwable throwable) throws Exception {
                        return true;
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(@NonNull Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
        );
    }


    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {
        mLifecycleSubject.onNext(RxLifeCycleEvent.ON_CREATE);
    }

    @Override
    public void onResume(Object target) {
        mLifecycleSubject.onNext(RxLifeCycleEvent.ON_RESUME);
    }

    @Override
    public void onPause(Object object) {
        mLifecycleSubject.onNext(RxLifeCycleEvent.ON_PAUSE);
    }

    @Override
    public void onDestroyView(Object target) {
        mLifecycleSubject.onNext(RxLifeCycleEvent.ON_DESTROY);
    }

    public Observable<RxLifeCycleEvent> getObservable() {
        return mLifecycleSubject.share();
    }

    private <T> RxLifecycleTransformer<T> bind(Observable<?> predicate) {
        return new RxLifecycleTransformer<>(predicate);
    }

    @Override
    public int getPriority() {
        return HIGH_PRIORITY;
    }
}
