package com.leroymerlin.pandroid.demo.main.rest;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.demo.globals.review.ReviewManager;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.future.NetActionDelegate;
import com.leroymerlin.pandroid.future.SimpleAsyncTaskLoader;
import com.leroymerlin.pandroid.ui.picture.PictureManager;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Inject;

/**
 * Created by florian on 07/01/16.
 */
public class RestFragment extends PandroidFragment<FragmentOpener> {

    @Inject
    ReviewManager reviewManager;
    @Inject
    ToastManager toastManager;

    //tag::Glide[]

    @Inject
    PictureManager pictureManager;

    //end::Glide[]


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rest, container, false);
    }

    //tag::Glide[]
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pictureManager.load("http://community.coreldraw.com/cfs-filesystemfile/__key/communityserver-components-secureimagefileviewer/telligent-evolution-components-attachments-13-393-00-00-00-14-32-80/Pandroid.JPG_2D00_1004x819.jpg",
                (ImageView) view.findViewById(R.id.rest_iv));
    }
    //end::Glide[]


    @Override
    public void onResume() {
        super.onResume();
        //tag::Retrofit[]
        //If the CancellableActionDelegate/NetActionDelegate is cancelled the retrofit request will be cancelled too and the result ignored
        //Note that the Delegate is register to the Fragment and will be cancel by the PandroidDelegate for you if the fragment is paused
        reviewManager.getReview("1", new NetActionDelegate<Review>(this) {
            @Override
            public void success(Review result) {
                toastManager.makeToast(getActivity(), result.getTitle(), null);
            }

            //tag::toastUsageExample[]
            @Override
            public void onNetworkError(int statusCode, String errorMessage, String body, Exception e) {
                toastManager.makeToast(getActivity(), errorMessage, null, R.style.Toast_Error);
            }
            //end::toastUsageExample[]
        });
        //end::Retrofit[]

        //tag::Loader[]
        Bundle b = new Bundle();
        b.putDouble("fibonacci", 28);
        getLoaderManager().initLoader(0, b, new LoaderManager.LoaderCallbacks<Double>() {
            @Override
            public Loader<Double> onCreateLoader(int id, Bundle args) {
                final double fibonacci = args.getDouble("fibonacci");
                return new SimpleAsyncTaskLoader<Double>(getActivity()) {
                    @Override
                    public Double loadInBackground() {
                        return fibonacci(fibonacci);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Double> loader, Double data) {
                toastManager.makeToast(getActivity(), "Result : "+data, null);
            }

            @Override
            public void onLoaderReset(Loader<Double> loader) {

            }
        });
        //end::Loader[]

    }

    private static double fibonacci(double n) {
        if (n == 1) {
            return 1;
        }
        else if (n == 2) {
            return 2;
        }
        else {
            return fibonacci(n-1) + fibonacci(n-2);
        }
    }
}
