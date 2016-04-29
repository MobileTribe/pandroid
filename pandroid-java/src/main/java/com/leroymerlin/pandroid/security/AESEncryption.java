package com.leroymerlin.pandroid.security;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by florian on 14/03/16.
 */
public class AESEncryption {

    static Logger logger = Logger.getLogger("AESEncryption");

    static String AES = "AES/ECB/PKCS5Padding";


    private static byte[] get256BitsKey(String seed) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(seed.getBytes("UTF-8"));
        return Arrays.copyOf(md.digest(), 16);
    }



    public static String symetricEncrypt(String seed, String data) {
        try {
            byte[] encryptionKey = get256BitsKey(seed);
            SecretKey key = new SecretKeySpec(encryptionKey, "AES");
            byte[] clearText = data.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64Support.encode(cipher.doFinal(clearText), Base64Support.NO_WRAP), "UTF-8");
        } catch (Exception e) {
            ((Logger) logger).warning(e.toString());
        }
        return null;
    }

    public static String symetricDecrypt(String seed, String encryptedData) {
        try {
            byte[] encryptionKey = get256BitsKey(seed);
            SecretKey key = new SecretKeySpec(encryptionKey, "AES");
            byte[] encrypedPwdBytes = Base64Support.decode(encryptedData, Base64Support.NO_WRAP);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));
            return new String(decrypedValueBytes, "UTF-8");
        } catch (Exception e) {
            ((Logger) logger).warning(e.toString());
        }
        return null;
    }

}
