package com.leroymerlin.pandroid.demo.globals.review;

import com.leroymerlin.pandroid.future.NetActionDelegate;
import com.leroymerlin.pandroid.demo.globals.model.Review;

/**
 * Created by florian on 16/06/15.
 */
public interface ReviewManager {

    void getReview(String productId, NetActionDelegate<Review> delegate);
}
