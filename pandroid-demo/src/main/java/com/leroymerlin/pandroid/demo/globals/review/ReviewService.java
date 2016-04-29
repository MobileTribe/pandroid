package com.leroymerlin.pandroid.demo.globals.review;

import com.leroymerlin.pandroid.demo.globals.model.Review;
import com.leroymerlin.pandroid.net.PandroidCall;
import com.leroymerlin.pandroid.net.http.Mock;
import com.leroymerlin.pandroid.net.mock.ServiceMock;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by florian on 07/01/16.
 */
//tag::Retrofit[]
public interface ReviewService {

    @GET("posts/{id}")
    Call<Review> getReview(@Path("id") String id);


    //Mocks have to enable in PandroidCallFactory
    @Mock(
            enable = true, //mock can be desable one by one
            delay = 400,   //delay before response
            statusCode = 200, //response statusCode
            mockClass = ServiceMock.class, //Mock class to change response dynamically
            path = "mock/reviews.json" //path to response json file in your assets
    )
    @GET("posts/{id}")
    PandroidCall<Review> getReviewPandroidWay(@Path("id") String id);

}
//end::Retrofit[]
