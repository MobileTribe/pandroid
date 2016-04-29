package com.leroymerlin.pandroid.security;

import android.test.suitebuilder.annotation.SmallTest;

import com.leroymerlin.pandroid.demo.ApplicationTest;
import com.leroymerlin.pandroid.log.PandroidLogger;

import junit.framework.Assert;

import java.util.UUID;

/**
 * Created by florian on 13/01/16.
 */
public class SecurityTest extends ApplicationTest {

    @SmallTest
    public void testEncryptionManager() {
        RsaAesCryptoManager rsaAesCryptoManager = new RsaAesCryptoManager(getContext(), PandroidLogger.getInstance());
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
