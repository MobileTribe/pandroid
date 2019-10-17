package com.leroymerlin.pandroid.demo.main.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Keep;

import com.leroymerlin.pandroid.app.delegate.impl.IcepickLifecycleDelegate;
import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.demo.globals.review.ReviewManager;
import com.leroymerlin.pandroid.future.NetActionDelegate;
import com.leroymerlin.pandroid.mvp.Presenter;

import javax.inject.Inject;


/**
 * Created by Mehdi on 08/11/2016.
 */
public class TestPresenter extends Presenter<TestPresenter.TestPresenterView> {

    ReviewManager mReviewManager;

    @Keep
    Review review;

    @Inject
    public TestPresenter(ReviewManager reviewManager) {
        super();
        addLifecycleDelegate(new IcepickLifecycleDelegate());
        mReviewManager = reviewManager;
    }

    @Override
    public void onInit(TestPresenterView target) {
        super.onInit(target);
    }


    @Override
    public void onCreateView(TestPresenterView target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        load();
    }

    void load() {

        NetActionDelegate<Review> delegate = new NetActionDelegate<Review>(this) {

            @Override
            protected void success(Review result) {
                review = result;
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
        };
        if (review != null) {
            delegate.onSuccess(review);
        } else {
            mReviewManager.getReview("1", delegate);
        }
    }

    interface TestPresenterView {
        void onDataLoaded(String value);

        void onError(String error);
    }


}
