package com.leroymerlin.pandroid.event;

import android.test.suitebuilder.annotation.SmallTest;

import com.leroymerlin.pandroid.demo.ApplicationTest;

import junit.framework.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * Created by florian on 19/01/16.
 */
public class EventTests extends ApplicationTest {

    private EventBusManagerImpl eventBusManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        eventBusManager = new EventBusManagerImpl();
    }

    @SmallTest
    public void testTaggedReceiver() {
        final String sendedObject = "test";
        final int[] received = {0};

        //TODO
        eventBusManager.registerReceiver(new EventBusManager.EventBusReceiver() {
            @Override
            public List<String> getTags() {
                return Arrays.asList("TEST");
            }

            @Override
            public boolean handle(Object data) {
                Assert.assertEquals(data, sendedObject);
                received[0]++;
                return true;
            }
        });
        eventBusManager.sendSync(sendedObject, "TEST");
        assertEquals(received[0], 0);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        assertEquals(received[0], 1);
    }
}
