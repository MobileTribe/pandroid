package com.leroymerlin.pandroid;

import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.EventBusManagerImpl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by florian on 01/02/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EventUnitTest {


    private EventBusManagerImpl eventBusManager;
    private EventBusManager.EventBusReceiver taggedListener;
    static final String GOOD_FILTER = "GOOD_FILTER";

    @Before
    public void initTest() {
        eventBusManager = new EventBusManagerImpl();
        resultObject = null;

        taggedListener = new EventBusManager.EventBusReceiver() {
            @Override
            public List<String> getTags() {
                return Arrays.asList(GOOD_FILTER, "OTHER_FILTER");
            }

            @Override
            public boolean handle(Object data) {
                resultObject = data;
                return true;
            }
        };
    }

    private CountDownLatch lock = new CountDownLatch(1);


    private Object resultObject;

    @Test
    public void testEventAsync() throws Exception {
        eventBusManager.registerReceiver(new EventBusManager.EventBusReceiver() {
            @Override
            public List<String> getTags() {
                return null;
            }

            @Override
            public boolean handle(Object data) {
                resultObject = data;
                lock.countDown();
                return true;
            }
        });
        String test = "TEST";
        eventBusManager.send(test);
        lock.await(2000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(test, resultObject);

    }

    @Test
    public void testEventSync() throws Exception {
        eventBusManager.registerReceiver(taggedListener);
        String test = "testEventSync";
        eventBusManager.send(test, GOOD_FILTER);
        Assert.assertEquals(test, resultObject);
    }

    @Test
    public void testListenerRemove() {
        eventBusManager.registerReceiver(taggedListener);
        eventBusManager.unregisterReceiver(taggedListener);
        String test = "testListenerRemove";
        eventBusManager.sendSync(test);
    }

    @Test
    public void testListenerTag() {
        EventBusManager.EventBusReceiver wrongListener = new EventBusManager.EventBusReceiver() {
            @Override
            public List<String> getTags() {
                return Arrays.asList("WRONG_FILTER");
            }

            @Override
            public boolean handle(Object data) {
                if (data.equals("testListenerTag_GOOD_FILTER"))
                    Assert.fail();
                return true;
            }
        };
        eventBusManager.registerReceiver(wrongListener);
        eventBusManager.registerReceiver(taggedListener);
        String test = "testListenerTag";
        eventBusManager.sendSync(test, null, EventBusManager.DeliveryPolicy.UNCHECKED);
        Assert.assertNotSame(resultObject, test);

        test = "testListenerTag_GOOD_FILTER";
        eventBusManager.sendSync(test, "GOOD_FILTER");
        Assert.assertEquals(resultObject, test);
    }


    @Test
    public void testDeliveryPolicyAtLeastOne() {
        String test = "testDeliveryPolicyDefault";
        eventBusManager.sendSync(test, GOOD_FILTER, EventBusManager.DeliveryPolicy.AT_LEAST_ONE );
        eventBusManager.registerReceiver(taggedListener);
        Assert.assertEquals(resultObject, test);
        eventBusManager.unregisterReceiver(taggedListener);
        resultObject = null;
        eventBusManager.registerReceiver(taggedListener);
        Assert.assertNotSame(resultObject, test);
    }

    @Test
    public void testDeliveryPolicyUnlimited() {
        String test = "testDeliveryPolicyUnlimited";
        String id = eventBusManager.sendSync(test, GOOD_FILTER, EventBusManager.DeliveryPolicy.UNLIMITED);
        eventBusManager.registerReceiver(taggedListener);
        Assert.assertEquals(resultObject, test);
        resultObject = null;
        eventBusManager.unregisterReceiver(taggedListener);
        eventBusManager.registerReceiver(taggedListener);
        Assert.assertEquals(resultObject, test);
        resultObject = null;
        eventBusManager.unregisterReceiver(taggedListener);
        eventBusManager.cancel(id);
        resultObject = null;
        eventBusManager.registerReceiver(taggedListener);
        Assert.assertNotSame(resultObject, test);

        //check event not delivered twice to the same receiver
        eventBusManager.sendSync(test, GOOD_FILTER, EventBusManager.DeliveryPolicy.UNLIMITED);
        resultObject = null;
        eventBusManager.registerReceiver(new EventBusManager.EventBusReceiver() {
            @Override
            public List<String> getTags() {
                return null;
            }

            @Override
            public boolean handle(Object data) {
                return false;
            }
        });
        Assert.assertNotSame(resultObject, test);


    }

    @Test
    public void testDeliveryPolicyUnchecked() {
        String test = "testDeliveryPolicyUnchecked";
        eventBusManager.sendSync(test, null);
        eventBusManager.registerReceiver(taggedListener);
        Assert.assertNotSame(resultObject, test);
    }

}
