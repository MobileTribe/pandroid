package com.leroymerlin.pandroid.demo.main.mvp;

import android.app.Application;

import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.demo.globals.review.ReviewManager;
import com.leroymerlin.pandroid.demo.main.presenter.Presenter;
import com.leroymerlin.pandroid.future.NetActionDelegate;

import javax.inject.Inject;

/**
 * Created by Mehdi on 08/11/2016.
 */
public class TestPresenter extends Presenter<TestPresenter.TestPresenterView> {

    private ReviewManager mReviewManager;

    @Inject
    public TestPresenter(Application application, ReviewManager reviewManager) {
        super(application);
        mReviewManager = reviewManager;
    }

    public void load() {
        mReviewManager.getReview("1", new NetActionDelegate<Review>(this) {

            @Override
            protected void success(Review result) {
                final TestPresenterView view = getView();
                if (view != null) {
                    view.onDataLoaded(result.getBody());
                }
            }

            @Override
            protected void onNetworkError(int statusCode, String errorMessage, String body, Exception e) {
                TestPresenterView view = getView();
                if (view != null) {
                    view.onError(errorMessage);
                }
            }
        });
    }

    interface TestPresenterView {
        void onDataLoaded(String value);
        void onError(String error);
    }


}
