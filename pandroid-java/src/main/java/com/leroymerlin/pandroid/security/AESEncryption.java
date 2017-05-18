package com.leroymerlin.pandroid.security;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by florian on 14/03/16.
 */
public class AESEncryption {

    private static final String AES_ALGO = "AES";
    private static final String AES_FORMAT = "AES/CBC/PKCS5PADDING";
    private final static Random random = new Random();

    public static String symetricEncrypt(String seed, String data) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException {
        //return CryptoUtils.encrypt(seed, value);
        byte[] encryptionKey = get128BitsKey(seed);
        byte[] clearText = data.getBytes("UTF-8");

        SecretKey key = new SecretKeySpec(encryptionKey, AES_ALGO);
        // Cipher is not thread safe
        Cipher cipher = Cipher.getInstance(AES_FORMAT);

        byte[] vectorBytes = get16BitsKey();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(vectorBytes);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] encodedValue = cipher.doFinal(clearText);

        byte[] combined = new byte[encodedValue.length + vectorBytes.length];

        System.arraycopy(encodedValue, 0, combined, 0, encodedValue.length);
        System.arraycopy(vectorBytes, 0, combined, encodedValue.length, vectorBytes.length);

        return new String(Base64Support.encode(combined, Base64Support.NO_WRAP), "UTF-8");
    }

    public static String symetricDecrypt(String seed, String encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException {
        byte[] encryptionKey = get128BitsKey(seed);
        SecretKey key = new SecretKeySpec(encryptionKey, AES_ALGO);
        byte[] encrypedPwdBytes = Base64Support.decode(encryptedData, Base64Support.NO_WRAP);

        int vectorSize = 16;
        int spiteByteIndex = encrypedPwdBytes.length - vectorSize;

        IvParameterSpec ivParameterSpec = new IvParameterSpec(encrypedPwdBytes, spiteByteIndex, vectorSize);
        // cipher is not thread safe
        Cipher cipher = Cipher.getInstance(AES_FORMAT);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] decrypedValueBytes = cipher.doFinal(Arrays.copyOf(encrypedPwdBytes, spiteByteIndex));
        return new String(decrypedValueBytes, "UTF-8");

    }

    private static byte[] get128BitsKey(String seed) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(seed.getBytes());
        return Arrays.copyOf(md.digest(), 16);
    }


    private static byte[] get16BitsKey() throws NoSuchAlgorithmException {
        byte[] b = new byte[16];
        random.nextBytes(b);
        return b;
    }
}
