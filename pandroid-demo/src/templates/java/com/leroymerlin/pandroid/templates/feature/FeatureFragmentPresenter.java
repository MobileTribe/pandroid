package com.leroymerlin.pandroid.templates.feature;

import com.leroymerlin.pandroid.app.delegate.impl.IcepickLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.rx.RxLifecycleDelegate;
import com.leroymerlin.pandroid.mvp.Presenter;
import com.leroymerlin.pandroid.mvp.RxPresenter;
import com.leroymerlin.pandroid.templates.feature.managers.FeatureManager;
import com.leroymerlin.pandroid.templates.feature.models.FeatureModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class FeatureFragmentPresenter extends RxPresenter<FeatureFragmentPresenter.PresenterView> {

    @Inject
    FeatureManager featureManager; //TODO implement this manager

    @State
    List<FeatureModel> cachedValue;

    public FeatureFragmentPresenter() {
        super();
        addLifecycleDelegate(new IcepickLifecycleDelegate());
    }

    private Single<List<FeatureModel>> loadData() {
        if (cachedValue != null) {
            return Single.just(cachedValue);
        }
        return featureManager.loadData()
                .toList();
    }

    @Override
    public void onResume(PresenterView target) {
        super.onResume(target);
        reload();
    }

    public void reload() {
        PresenterView target = this.targetRef.get();
        loadData()
                .compose(this.bindLifecycle())
                .doOnSubscribe(featureModels -> target.showLoader())
                .doOnDispose(target::hideLoader)
                .doOnError((throwable) -> target.hideLoader())
                .doOnSuccess(featureModels -> {
                    this.cachedValue = featureModels;
                    target.hideLoader();
                }).subscribe(
                target::onDataLoaded,
                throwable -> target.onError(throwable.getMessage())
        );
    }

    interface PresenterView {

        void showLoader();

        void hideLoader();

        void onError(String message);

        void onDataLoaded(List<FeatureModel> featureModel);
    }

}
