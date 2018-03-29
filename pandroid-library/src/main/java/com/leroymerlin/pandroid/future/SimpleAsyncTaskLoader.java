package com.leroymerlin.pandroid.future;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by paillardf on 03/07/2014.
 */
public abstract class SimpleAsyncTaskLoader<T> extends AsyncTaskLoader<T> {
    public SimpleAsyncTaskLoader(Context context) {
        super(context);
    }

    private T data;

    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            //releaseResources(data);
            return;
        }
        this.data = data;
        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }
    }


    @Override
    protected void onStartLoading() {
        if (data != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(data);
        }

        if (takeContentChanged() || data == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }


    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (data != null) {
            releaseData(data);
            data = null;
        }
    }

    /**
     * Call when the loader is reset and data is set. At this point we can release the resources associated with 'data'.
     */
    protected void releaseData(T data){

    }
}
