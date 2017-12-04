package com.leroymerlin.pandroid.event;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.leroymerlin.pandroid.demo.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by florian on 19/01/16.
 */
@RunWith(AndroidJUnit4.class)
public class EventTests {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);



    private EventBusManagerImpl eventBusManager;

    @Before
    public void setUp() throws Exception {
        eventBusManager = new EventBusManagerImpl();
    }

    @Test
    public void testTaggedReceiver() {
        final String sendedObject = "test";
        final int[] received = {0};

        eventBusManager.registerReceiver(new EventBusManager.EventBusReceiver() {
            @Override
            public List<String> getTags() {
                return Arrays.asList("TEST");
            }

            @Override
            public boolean handle(String tag, Object data) {
                assertEquals(data, sendedObject);
                received[0]++;
                return true;
            }
        });
        eventBusManager.send(sendedObject, "TEST");
        assertEquals(received[0], 0);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        assertEquals(received[0], 1);
    }
}
