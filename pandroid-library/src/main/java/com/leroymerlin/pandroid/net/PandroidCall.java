package com.leroymerlin.pandroid.net;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.future.ActionDelegate;

import retrofit2.Call;

/**
 * Created by florian on 08/01/16.
 */
public interface PandroidCall<T> extends Call<T>{

    void enqueue(ActionDelegate<T> callback);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    PandroidCall<T> clone();
}
