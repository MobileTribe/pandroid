package com.leroymerlin.pandroid.demo.globals.review;

import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.future.NetActionDelegate;
import com.leroymerlin.pandroid.net.PandroidCall;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by florian on 16/06/15.
 */
@Singleton
public class ReviewManagerImpl implements ReviewManager {


    @Inject
    ReviewService reviewService;

    @Inject
    ReviewManagerImpl() {
    }

    //tag::Retrofit[]
    @Override
    public void getReview(String productId, final NetActionDelegate<Review> delegate) {

        //Standard Retrofit way
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

        //Pandroid way
        PandroidCall<Review> reviewPandroidWay = reviewService.getReviewPandroidWay(productId);
        reviewPandroidWay.enqueue(delegate);
    }
    //end::Retrofit[]

}
