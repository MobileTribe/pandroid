package com.leroymerlin.pandroid.future;

/**
 * Created by florian on 12/02/15.
 */
public interface Cancellable {

    void cancel();

    boolean isCancelled();

    interface CancellableRegister {
        void registerDelegate(Cancellable delegate);

        boolean unregisterDelegate(Cancellable delegate);
    }
}
