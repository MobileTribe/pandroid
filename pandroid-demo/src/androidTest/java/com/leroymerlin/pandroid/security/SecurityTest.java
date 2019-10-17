package com.leroymerlin.pandroid.security;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.leroymerlin.pandroid.demo.main.MainActivity;
import com.leroymerlin.pandroid.log.PandroidLogger;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

/**
 * Created by florian on 13/01/16.
 */

@RunWith(AndroidJUnit4.class)
public class SecurityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testEncryptionManager() {
        RsaAesCryptoManager rsaAesCryptoManager = new RsaAesCryptoManager(activityRule.getActivity(), PandroidLogger.getInstance());
        String key = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();

        String encryptedValue = rsaAesCryptoManager.symetricEncrypt(key, value);
        Assert.assertNotSame(value, encryptedValue);
        Assert.assertEquals(value, rsaAesCryptoManager.symetricDecrypt(key, encryptedValue));


        encryptedValue = rsaAesCryptoManager.asymmetricEncrypt(value);
        Assert.assertNotSame(value, encryptedValue);
        Assert.assertEquals(value, rsaAesCryptoManager.asymmetricDecrypt(encryptedValue));
    }
}
