package com.leroymerlin.pandroid.future;

/**
 * Created by Florian on 17/06/2014.
 */
public class ActionDelegateWrapper<T> implements ActionDelegate<T> {

    protected int rest;
    protected final int count;
    protected final ActionDelegate<T> delegate;
    protected T lastResult;
    protected Exception lastError;

    public ActionDelegateWrapper(ActionDelegate<T> delegate, int count) {
        this.count = count;
        this.delegate = delegate;
    }

    @Override
    public void onError(Exception e) {
        lastError = e;
        next();
    }


    @Override
    public void onSuccess(T result) {
        lastResult = result;
        next();
    }

    protected void next() {
        rest++;
        if (delegate instanceof ProgressActionDelegate)
            ((ProgressActionDelegate) delegate).onProgress((int) (((float) rest) / count * 100));

        if (rest == count)
            publishResult();
    }

    protected void publishResult() {
        if (lastError != null)
            delegate.onError(lastError);
        else
            delegate.onSuccess(lastResult);

    }
}
