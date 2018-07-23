package com.leroymerlin.pandroid.future;


import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierActionDelegate<T> implements ActionDelegate<T> {
    private static final String TAG = CyclicBarrierActionDelegate.class.getSimpleName();
    LogWrapper logWrapper = PandroidLogger.getInstance();

    private CyclicBarrier barrier = new CyclicBarrier(2);

    private T result;
    private Exception exception;

    /**
     * Wait until action is done or time out of 60 seconds is reach
     *
     * @return action result
     * @throws Exception
     */
    public T waitDone() throws Exception {
        return waitDone(60);
    }

    /**
     * Wait until action is done or time out is reach
     *
     * @param timeOut in seconds
     * @return action result
     * @throws Exception
     */
    public T waitDone(int timeOut) throws Exception {
        barrier.await(timeOut, TimeUnit.SECONDS);
        if (exception != null)
            throw exception;
        return result;
    }

    @Override
    public void onSuccess(T result) {
        this.result = result;

        try {
            barrier.await();
        } catch (Exception e) {
            logWrapper.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onError(Exception e) {
        this.exception = e;
        try {
            barrier.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}
