package com.leroymerlin.pandroid.demo.main.rx;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.RxPandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.demo.globals.review.ReviewService;
import com.leroymerlin.pandroid.demo.globals.review.RxReviewManager;
import com.leroymerlin.pandroid.event.opener.FragmentOpener;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by florian on 20/06/2017.
 */

//tag::RxWrapper[]
public class RxFragment extends RxPandroidFragment<FragmentOpener> {
    private static final String TAG = "RxFragment";
    @Inject
    RxReviewManager reviewManager;

    //end::RxWrapper[]

    @Inject
    ToastManager toastManager;
    @Inject
    ReviewService reviewService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rx, container, false);
    }


    //tag::RxWrapper[]
    @Override
    public void onResume() {
        super.onResume();

        reviewManager.rxGetLastReview()
                .flatMap(reviewResult -> reviewResult.result != null ? Single.just(reviewResult.result) : reviewManager.rxGetReview("1"))
                .compose(this.<Review>bindLifecycleObserveOnMain())
                .subscribe(review -> {
                    //we are sure fragment is still not detached thanks to the compose
                    //no need to check getActivity()!=null
                    toastManager.makeToast(getActivity(), review.getTitle(), null);
                }, throwable -> {
                    logWrapper.w(TAG, throwable);
                });

        //end::RxWrapper[]

        //tag::RxAndroid[]
        //We can cast to RxPandroidCall if rxandroid is enable in the plugin configuration
        reviewService.getReview("1")
                .rxEnqueue()
                //bind observer on lifecycle thanks to RxPandroidFragment
                .compose(this.<Review>bindLifecycle())
                .subscribe(o -> {
                    //we can access getActivity() with no check because call will be cancel if our app is paused
                    toastManager.makeToast(getActivity(), o.getTitle(), null);
                }, throwable -> {
                    logWrapper.w(TAG, throwable);
                });
        //end::RxAndroid[]

    }

}
