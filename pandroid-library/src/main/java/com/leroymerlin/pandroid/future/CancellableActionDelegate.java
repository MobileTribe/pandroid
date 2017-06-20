package com.leroymerlin.pandroid.future;

import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paillard.f on 21/02/2014.
 */
public abstract class CancellableActionDelegate<T> implements ProgressActionDelegate<T>, Cancellable {

    private String TAG = "CancellableActionDelegate";
    LogWrapper logWrapper = PandroidLogger.getInstance();

    protected boolean cancel;
    private List<CancelListener> listeners = new ArrayList<>();

    protected abstract void success(T result);

    protected abstract void error(Exception e);


    @Override
    public void cancel() {
        this.cancel = true;
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onCancel();
            listeners.remove(i);
        }
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void onSuccess(T result) {
        if (!isCancelled()) {
            success(result);
        } else {
            logWrapper.d(TAG, "Action cancelled: Result ignored");
        }

    }

    @Override
    public void onProgress(int percent) {

    }

    @Override
    public void onError(Exception e) {
        if (!isCancelled()) {
            error(e);
        } else {
            logWrapper.d(TAG, "Action cancelled: Error ignored", e);
        }
    }

    public void addCancelListener(CancelListener listener) {
        this.listeners.add(listener);
    }

    public void removeCancelListener(CancelListener listener) {
        listeners.remove(listener);
    }

    public interface CancelListener {
        void onCancel();
    }

    public CancellableActionDelegate<T> register(CancellableRegister register) {
        register.registerDelegate(this);
        return this;
    }



}
