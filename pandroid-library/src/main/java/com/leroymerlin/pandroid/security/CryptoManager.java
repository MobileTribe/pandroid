package com.leroymerlin.pandroid.security;

/**
 * Created by florian on 05/01/16.
 */
public interface CryptoManager {

    String asymmetricEncrypt(String data);

    String asymmetricDecrypt(String encryptedData);

    String symetricEncrypt(String seed, String data);

    String symetricDecrypt(String seed, String encryptedData);
}
