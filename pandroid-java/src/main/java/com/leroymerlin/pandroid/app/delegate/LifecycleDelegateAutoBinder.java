package com.leroymerlin.pandroid.app.delegate;

/**
 * Created by Mehdi on 07/11/2016.
 */

public interface LifecycleDelegateAutoBinder {

    void bind();

    LifecycleDelegateAutoBinder EMPTY = new LifecycleDelegateAutoBinder() {
        @Override
        public void bind() {
            // do nothing
        }
    };

}
