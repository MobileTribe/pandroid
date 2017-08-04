package com.leroymerlin.pandroid;

import com.leroymerlin.pandroid.future.ActionDelegate;
import com.leroymerlin.pandroid.future.RxActionDelegate;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by florian on 01/02/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RxUnitTest {

    private static final String RESULT_VALUE = "RESULT_VALUE";

    @Test
    public void testRxActionDelegateSingle() throws InterruptedException {
        final CountDownLatch lock1 = new CountDownLatch(1);


        //check success
        RxActionDelegate.single(new RxActionDelegate.OnSubscribeAction<String>() {
            @Override
            public void subscribe(ActionDelegate<String> delegate) {
                delegate.onSuccess(RESULT_VALUE);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Assert.assertEquals(s, RESULT_VALUE);
                lock1.countDown();
            }
        });
        Assert.assertEquals(lock1.await(2000, TimeUnit.MILLISECONDS), true);


        //check error
        final CountDownLatch lock2 = new CountDownLatch(1);
        RxActionDelegate.single(new RxActionDelegate.OnSubscribeAction<String>() {
            @Override
            public void subscribe(ActionDelegate<String> delegate) {
                delegate.onError(new Exception());
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Assert.fail("should not be called");
                lock2.countDown();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                lock2.countDown();
            }
        });
        Assert.assertEquals(lock2.await(2000, TimeUnit.MILLISECONDS), true);


        //check async call on success
        final CountDownLatch lock3 = new CountDownLatch(1);
        RxActionDelegate.single(new RxActionDelegate.OnSubscribeAction<String>() {
            @Override
            public void subscribe(ActionDelegate<String> delegate) {
                syncCallTest(delegate);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Assert.assertEquals(s, RESULT_VALUE);
                lock3.countDown();
            }
        });
        Assert.assertEquals(lock3.await(2000, TimeUnit.MILLISECONDS), true);


        //check dispose
        final CountDownLatch lock4 = new CountDownLatch(1);
        RxActionDelegate.single(new RxActionDelegate.OnSubscribeAction<String>() {
            @Override
            public void subscribe(ActionDelegate<String> delegate) {
                syncCallTest(delegate);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Assert.fail("should not be called");
                lock4.countDown();
            }
        }).dispose();
        Assert.assertEquals(lock4.await(2000, TimeUnit.MILLISECONDS), false);


    }

    private void syncCallTest(final ActionDelegate<String> actionDelegate) {
        Single.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                actionDelegate.onSuccess(RESULT_VALUE);
            }
        });
    }


    @Test
    public void testRxActionDelegateObservable() throws InterruptedException {

        final List<RxActionDelegate.Result> results = new ArrayList<>();

        final CountDownLatch lock = new CountDownLatch(4);


        RxActionDelegate.observableWrapped(new RxActionDelegate.OnSubscribeAction<String>() {
            @Override
            public void subscribe(ActionDelegate<String> delegate) {
                syncStreamTest(delegate);
            }
        }).subscribe(new Consumer<RxActionDelegate.Result<String>>() {
            @Override
            public void accept(@NonNull RxActionDelegate.Result<String> result) throws Exception {
                results.add(result);
                lock.countDown();
            }
        });
        Assert.assertEquals(lock.await(2000, TimeUnit.MILLISECONDS), true);

        Assert.assertEquals(results.get(0).result, RESULT_VALUE);
        Assert.assertEquals(results.get(1).result, RESULT_VALUE);
        Assert.assertEquals(results.get(2).result, RESULT_VALUE);
        Assert.assertNotNull(results.get(3).error);
        Assert.assertNull(results.get(3).result);


    }


    private void syncStreamTest(final ActionDelegate<String> actionDelegate) {
        Single.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                actionDelegate.onSuccess(RESULT_VALUE);
                actionDelegate.onSuccess(RESULT_VALUE);
                actionDelegate.onSuccess(RESULT_VALUE);
                actionDelegate.onError(new Exception());
                actionDelegate.onSuccess(RESULT_VALUE);
            }
        });
    }


}
