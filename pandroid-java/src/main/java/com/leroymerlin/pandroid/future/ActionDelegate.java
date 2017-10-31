package com.leroymerlin.pandroid.future;

public interface ActionDelegate<T> {

    void onSuccess(T result);

    void onError(Exception e);

}
