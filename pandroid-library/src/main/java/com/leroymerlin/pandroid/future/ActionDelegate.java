package com.leroymerlin.pandroid.future;

/**
 * Created by paillard.f on 21/02/2014.
 */
public interface ActionDelegate<T> {

    public void onSuccess(T result);

    public void onError(Exception e);


}
