package com.leroymerlin.pandroid.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import com.leroymerlin.pandroid.log.LogWrapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.security.auth.x500.X500Principal;

/**
 * Created by florian on 05/10/2015.
 */
@Singleton
public class RsaAesCryptoManager implements CryptoManager {
    private static final String PREF_FILE = ".ksdata";
    private static final String TAG = "RsaAesCryptoManager";
    public static final String RSA_FORMAT = "RSA/ECB/PKCS1Padding";
    private static final String KEY_ALIAS = "_k_";

    private final LogWrapper logWrapper;
    private KeyStore keyStore;
    Context context;


    @Inject
    public RsaAesCryptoManager(Context context, LogWrapper logWrapper) {
        this.context = context;
        this.logWrapper = logWrapper;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");

                keyStore.load(null);
            } catch (Exception e) {
                logWrapper.e(TAG, e);
            }
        }
        createNewKeys();
    }


    public void writeKey(KeyPair value) {
        try {
            FileOutputStream fos = null;
            fos = context.openFileOutput(PREF_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(value);
            os.close();
            fos.close();
        } catch (Exception e) {
            logWrapper.e(TAG, e);
        }
    }

    public KeyPair readKey() {
        try {
            FileInputStream fis = context.openFileInput(PREF_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);
            KeyPair keyPair = (KeyPair) is.readObject();
            is.close();
            fis.close();
            return keyPair;
        } catch (Exception e) {
            logWrapper.e(TAG, e);
            return null;
        }
    }

    private void createNewKeys() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                initializeKeystore();
            } else {
                if (readKey() == null) {
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(2048);
                    KeyPair keyPair = generator.generateKeyPair();
                    writeKey(keyPair);
                }
            }
        } catch (Exception e) {
            logWrapper.wtf(TAG, Log.getStackTraceString(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initializeKeystore() throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // Create new key if needed
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);
            KeyPairGeneratorSpec spec = null;
            spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            generator.initialize(spec);
            generator.generateKeyPair();
        }
    }

    private byte[] get256BitsKey(String seed) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(seed.getBytes());
        return md.digest();
    }

    @Override
    public String asymmetricEncrypt(String data) {
        try {
            Key publicKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
                publicKey = privateKeyEntry.getCertificate().getPublicKey();
            } else {
                publicKey = readKey().getPublic();
            }
            // Encrypt the text
            Cipher input = Cipher.getInstance(RSA_FORMAT);
            input.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = data.getBytes("UTF-8");
            return Base64.encodeToString(input.doFinal(bytes), Base64.NO_WRAP);
        } catch (Exception e) {
            logWrapper.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    public String asymmetricDecrypt(String encryptedData) {
        try {
            Key privateKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
                privateKey = privateKeyEntry.getPrivateKey();

            } else {
                privateKey = readKey().getPrivate();
            }

            Cipher output = Cipher.getInstance(RSA_FORMAT);
            output.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes = output.doFinal(Base64.decode(encryptedData, Base64.NO_WRAP));
            return new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            logWrapper.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    public String symetricEncrypt(String seed, String data) {
        try {
            //return CryptoUtils.encrypt(seed, value);
            byte[] encryptionKey = get256BitsKey(seed);

            SecretKey key = new SecretKeySpec(encryptionKey, "AES");

            byte[] clearText = data.getBytes("UTF-8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.encode(cipher.doFinal(clearText), Base64.NO_WRAP), "UTF-8");
        } catch (Exception e) {
            logWrapper.w(TAG, e);
        }
        return null;
    }

    @Override
    public String symetricDecrypt(String seed, String encryptedData) {
        try {
            byte[] encryptionKey = get256BitsKey(seed);

            SecretKey key = new SecretKeySpec(encryptionKey, "AES");
            ;
            byte[] encrypedPwdBytes = Base64.decode(encryptedData, Base64.NO_WRAP);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));
            return new String(decrypedValueBytes, "UTF-8");
        } catch (Exception e) {
            logWrapper.w(TAG, e);
        }

        return null;
    }
}
