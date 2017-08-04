package com.leroymerlin.pandroid.future;

/**
 * Created by paillard.f on 21/02/2014.
 */
public interface ActionDelegate<T> {

    void onSuccess(T result);

    void onError(Exception e);

}
