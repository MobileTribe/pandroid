package com.leroymerlin.pandroid.demo.main.rx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.demo.globals.review.RxReviewManager;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.future.RxActionDelegate;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * Created by florian on 20/06/2017.
 */

public class RxFragment extends PandroidFragment<FragmentOpener> {


    @Inject
    RxReviewManager reviewManager;

    @Inject
    ToastManager toastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rx, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        reviewManager.rxGetLastReview().flatMap(new Function<RxActionDelegate.Result<Review>, SingleSource<Review>>() {
            @Override
            public SingleSource<Review> apply(@NonNull RxActionDelegate.Result<Review> reviewResult) throws Exception {
                return reviewResult.result != null ? Single.just(reviewResult.result) : reviewManager.rxGetReview("1");
            }
        }).subscribe(new Consumer<Review>() {
            @Override
            public void accept(@NonNull Review review) throws Exception {
                if (getActivity() != null) {
                    toastManager.makeToast(getActivity(), review.getTitle(), null);
                }
            }
        });
    }
}
