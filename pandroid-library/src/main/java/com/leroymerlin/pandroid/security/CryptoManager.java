package com.leroymerlin.pandroid.security;

import com.leroymerlin.pandroid.annotations.RxWrapper;

/**
 * Created by florian on 05/01/16.
 */
public interface CryptoManager {

    @RxWrapper(wrapResult = true)
    String asymmetricEncrypt(String data);

    @RxWrapper(wrapResult = true)
    String asymmetricDecrypt(String encryptedData);

    @RxWrapper(wrapResult = true)
    String symetricEncrypt(String seed, String data);

    @RxWrapper(wrapResult = true)
    String symetricDecrypt(String seed, String encryptedData);
}
