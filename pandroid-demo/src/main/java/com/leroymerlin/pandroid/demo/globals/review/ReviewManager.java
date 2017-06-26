package com.leroymerlin.pandroid.demo.globals.review;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.future.ActionDelegate;
import com.leroymerlin.pandroid.future.NetActionDelegate;
import com.leroymerlin.pandroid.demo.globals.model.Review;

/**
 * Created by florian on 16/06/15.
 */
public interface ReviewManager {

    @RxWrapper
    void getReview(String productId, ActionDelegate<Review> delegate);

    @RxWrapper(wrapResult = true)
    Review getLastReview();
}
