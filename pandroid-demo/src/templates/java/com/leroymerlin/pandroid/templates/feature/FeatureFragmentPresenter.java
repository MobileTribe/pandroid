package com.leroymerlin.pandroid.templates.feature;

import com.leroymerlin.pandroid.app.delegate.impl.IcepickLifecycleDelegate;
import com.leroymerlin.pandroid.mvp.RxPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FeatureFragmentPresenter extends RxPresenter<FeatureFragmentPresenter.PresenterView> {

    @Inject
    FeatureManager featureManager; //TODO implement this manager

    @State
    ArrayList<FeatureModel> cachedValue;

    @Override
    protected void initNestedLifecycleDelegate() {
        super.initNestedLifecycleDelegate();
        addLifecycleDelegate(new IcepickLifecycleDelegate());
    }

    private Single<List<FeatureModel>> loadData() {
        if (cachedValue != null) {
            return Single.just(cachedValue);
        }
        return featureManager
                .loadData()
                .toList();
    }

    @Override
    public void onResume(PresenterView target) {
        super.onResume(target);
        reload();
    }

    public void reload() {
        PresenterView target = getView();
        loadData()
                .compose(this.bindLifecycleObserveOnMain())
                .doOnSubscribe(featureModels -> target.showLoader())
                .doOnDispose(target::hideLoader)
                .doOnError((throwable) -> target.hideLoader())
                .doOnSuccess(featureModels -> {
                    this.cachedValue = new ArrayList<>(featureModels);
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
