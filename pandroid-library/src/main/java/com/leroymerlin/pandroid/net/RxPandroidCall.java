package com.leroymerlin.pandroid.net;

import com.leroymerlin.pandroid.future.ActionDelegate;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Call;

/**
 * Created by florian on 08/01/16.
 */
public interface RxPandroidCall<T> extends PandroidCall<T>{

    Single<T> rxEnqueue();

    Completable rxCompleteEnqueue();

    RxPandroidCall<T> clone();
}
