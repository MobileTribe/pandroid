package com.leroymerlin.pandroid.future;

import com.leroymerlin.pandroid.net.NetworkException;

/**
 * Created by paillard.f on 21/02/2014.
 */
public abstract class NetActionDelegate<T> extends CancellableActionDelegate<T> {

    private static String TAG = "NetActionDelegate";


    public NetActionDelegate(ActionDelegateRegister actionDelegateRegister) {
        super();
        actionDelegateRegister.registerDelegate(this);
    }


    protected abstract void success(T result);

    @Override
    public void onError(Exception e) {
        if (!isCancelled()) {
            error(e);
        } else {
            logWrapper.d(TAG, "Action cancelled: Error ignored", e);
        }
    }

    @Override
    protected void error(Exception e) {
        if (e instanceof NetworkException) {
            NetworkException nError = (NetworkException) e;
            onNetworkError(nError.getStatusCode(), nError.getErrorMessage(), nError.getBody(), e);
        } else {
            logWrapper.w(TAG, "NetActionDelegate error should be NetworkException", e);
            onNetworkError(0, e.getMessage(), "", e);
        }
    }

    protected abstract void onNetworkError(int statusCode, String errorMessage, String body, Exception e);


}
