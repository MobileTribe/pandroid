package com.leroymerlin.pandroid.future;

/**
 * Created by florian on 12/02/15.
 */
public interface Cancellable {

    void cancel();

    boolean isCancelled();
}
