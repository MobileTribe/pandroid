package com.leroymerlin.pandroid;

import com.leroymerlin.pandroid.future.ActionDelegate;
import com.leroymerlin.pandroid.future.ChainedActionDelegate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

/**
 * Created by florian on 01/02/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DelegatesTest {


    @Before
    public void initTest() {

    }

    static final String STRING_RESULT = "String value";
    static final List<String> STRINGS_RESULT = Arrays.asList("TOTO", "TOTO1", "TOTO2");

    void asyncStringCall(ActionDelegate<String> delegate) {
        delegate.onSuccess(STRING_RESULT);
    }

    void asyncStringCallWithError(ActionDelegate<String> delegate) {
        delegate.onError(new Exception(STRING_RESULT));
    }

    void asyncStringsCall(ActionDelegate<List<String>> delegate) {
        delegate.onSuccess(STRINGS_RESULT);
    }

    List<String> finalResult;
    @Test
    public void testChaining() {
        finalResult = null;

        ChainedActionDelegate.create(new ChainedActionDelegate.StartDelegate<String>() {
            @Override
            public void start(ActionDelegate<String> next) {
                asyncStringCall(next);
            }
        }).then(new ChainedActionDelegate.SuccessDelegate<String, List<String>>() {
            @Override
            public void success(String result, ActionDelegate<List<String>> next) {
                Assert.assertEquals(result, STRING_RESULT);
                asyncStringsCall(next);
            }
        }).then(new ChainedActionDelegate.SuccessDelegate<List<String>, String>() {
            @Override
            public void success(List<String> result, ActionDelegate<String> next) {
                Assert.assertArrayEquals(result.toArray(), STRINGS_RESULT.toArray());
                asyncStringCallWithError(next);
            }
        }).error(new ChainedActionDelegate.ErrorDelegate<String>() {
            @Override
            public void error(Exception e, ActionDelegate<String> next) {
                Assert.assertEquals(e.getMessage(), STRING_RESULT);
                asyncStringCall(next);
            }
        }).then(new ChainedActionDelegate.SuccessDelegate<String, List<String>>() {
            @Override
            public void success(String result, ActionDelegate<List<String>> next) {
                Assert.assertEquals(result, STRING_RESULT);
                asyncStringsCall(next);
            }
        }).finish(new ActionDelegate<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                finalResult = result;
            }

            @Override
            public void onError(Exception e) {
                Assert.fail("should not have exception");
            }
        }).start();
        Assert.assertArrayEquals(finalResult.toArray(), STRINGS_RESULT.toArray());

    }



    @Test
    public void testErrorChaining() {
        ChainedActionDelegate.create(new ChainedActionDelegate.StartDelegate<String>() {
            @Override
            public void start(ActionDelegate<String> next) {
                asyncStringCall(next);
            }
        }).error(new ChainedActionDelegate.ErrorDelegate<String>() {
            @Override
            public void error(Exception e, ActionDelegate<String> next) {
                Assert.fail("Should not get any error");
            }
        }).then(new ChainedActionDelegate.SuccessDelegate<String, String>() {
            @Override
            public void success(String result, ActionDelegate<String> next) {
                asyncStringCallWithError(next);
            }
        }).error(new ChainedActionDelegate.ErrorDelegate<String>() {
            @Override
            public void error(Exception e, ActionDelegate<String> next) {
               Assert.assertEquals(e.getMessage(), STRING_RESULT);
            }
        }).finish(new ActionDelegate<String>() {

            @Override
            public void onSuccess(String result) {
                Assert.fail("No success should be received");
            }

            @Override
            public void onError(Exception e) {
                Assert.fail("Error handle before");
            }
        }).start();

    }
}
