package com.leroymerlin.pandroid.future;

import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

/**
 * Created by paillard.f on 21/02/2014.
 */
public class NullDelegate<T> implements ActionDelegate<T> {
    private static final String TAG = "NullDelegate";

    LogWrapper logWrapper = PandroidLogger.getInstance();

    @Override
    public void onSuccess(T result) {
    }

    @Override
    public void onError(Exception ex) {
        logWrapper.e(TAG, ex.getMessage(), ex);
    }
}
