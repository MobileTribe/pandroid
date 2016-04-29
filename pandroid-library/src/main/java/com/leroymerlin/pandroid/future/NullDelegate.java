package com.leroymerlin.pandroid.future;

import android.util.Log;

/**
 * Created by paillard.f on 21/02/2014.
 */
public class NullDelegate<T> implements ActionDelegate<T> {
    private static final String TAG = "NullDelegate";

    @Override
    public void onSuccess(T result) {
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, ex.getMessage(), ex);
    }
}
