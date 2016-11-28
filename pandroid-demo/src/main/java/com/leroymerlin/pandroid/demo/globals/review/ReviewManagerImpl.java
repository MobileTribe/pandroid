package com.leroymerlin.pandroid.demo.globals.review;

import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.future.ActionDelegate;
import com.leroymerlin.pandroid.future.NetActionDelegate;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.net.PandroidCall;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;


/**
 * Created by florian on 16/06/15.
 */
@Singleton
public class ReviewManagerImpl implements ReviewManager {


    private static final String TAG = "ReviewManagerImpl";
    @Inject
    ReviewService reviewService;

    @Inject
    LogWrapper logWrapper;

    @Inject
    ReviewManagerImpl() {
    }

    //tag::Retrofit[]
    @Override
    public void getReview(String productId, final NetActionDelegate<Review> delegate) {

        //Standard Retrofit way
        /*
        Call<Review> review = reviewService.getReview(productId);
        review.enqueue(new Callback<Review>() {

            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                try {
                    if (response.isSuccessful()) {
                        delegate.onSuccess(response.body());
                    } else {
                        String string = response.errorBody().string();
                        delegate.onError(new Exception(response.code() + ": " + string));
                    }
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                //Be carefull with the onFailure. This method can be called by retrofit outside of the main thread
                //You can't update you UI directly
                //delegate.onError(new Exception("Fail to get product", t)); => Error
            }
        });
        */

        // Pandroid way

        // if you need the Retrofit Response object
        PandroidCall<Response<Review>> reviewReponsePandroidWay = reviewService.getReviewReponsePandroidWay(productId);
        reviewReponsePandroidWay.enqueue(new ActionDelegate<Response<Review>>() {
            @Override
            public void onSuccess(Response<Review> result) {
                //be careful the response could be an error code
                //you have to check it yourself
                if (result.isSuccessful())
                    assert result.body() instanceof Review;
            }

            @Override
            public void onError(Exception e) {
                logWrapper.e(TAG, e);
            }
        });

        //Easiest way to get your object
        //if the retrofit response is an error it will pass the onError method of the delegate
        PandroidCall<Review> reviewPandroidWay = reviewService.getReviewPandroidWay(productId);
        reviewPandroidWay.enqueue(delegate);
    }
    //end::Retrofit[]

}
