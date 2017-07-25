package com.leroymerlin.pandroid.demo.main.rx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.RxPandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.demo.globals.review.RxReviewManager;
import com.leroymerlin.pandroid.event.opener.FragmentOpener;
import com.leroymerlin.pandroid.future.RxActionDelegate;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * Created by florian on 20/06/2017.
 */

//tag::RxWrapper[]
public class RxFragment extends RxPandroidFragment<FragmentOpener> {
    @Inject
    RxReviewManager reviewManager;

    //end::RxWrapper[]


    @Inject
    ToastManager toastManager;

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
                .flatMap(new Function<RxActionDelegate.Result<Review>, SingleSource<Review>>() {
                    @Override
                    public SingleSource<Review> apply(@NonNull RxActionDelegate.Result<Review> reviewResult) throws Exception {
                        return reviewResult.result != null ? Single.just(reviewResult.result) : reviewManager.rxGetReview("1");
                    }
                })
                .compose(this.<Review>bindLifecycle())
                .subscribe(new Consumer<Review>() {
                    @Override
                    public void accept(@NonNull Review review) throws Exception {
                        //we are sure fragment is still not detached thanks to the compose
                        //no need to check getActivity()!=null
                        toastManager.makeToast(getActivity(), review.getTitle(), null);
                    }
                });

        //end::RxWrapper[]

    }

}
