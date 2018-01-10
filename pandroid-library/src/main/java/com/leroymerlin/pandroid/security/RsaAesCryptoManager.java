package com.leroymerlin.pandroid.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.leroymerlin.pandroid.log.LogWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import javax.crypto.Cipher;
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

    private static final String KEYPAIR_ALGO = "RSA";
    private static final String RSA_FORMAT = "RSA/ECB/PKCS1Padding";

    protected static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    protected String keyAlias = "_k_";

    protected final LogWrapper logWrapper;
    protected KeyStore keyStore;
    Context context;


    @Inject
    public RsaAesCryptoManager(Context context, LogWrapper logWrapper) {
        this.context = context;
        this.logWrapper = logWrapper;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                keyStore.load(null);
            } catch (Exception e) {
                logWrapper.e(TAG, e);
            }
        }
        createNewKeys();
    }

    protected void saveKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        try {
            FileOutputStream fos = context.openFileOutput(PREF_FILE + File.pathSeparator + "public.key", Context.MODE_PRIVATE);
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();

            // Store Private Key.
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    privateKey.getEncoded());
            fos = context.openFileOutput(PREF_FILE + File.pathSeparator + "private.key", Context.MODE_PRIVATE);
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
        } catch (Exception e) {
            logWrapper.e(TAG, e);
        }
    }

    protected KeyPair loadKeyPair() {
        try {
            String publicKeyPath = PREF_FILE + File.pathSeparator + "public.key";
            // Read Public Key.
            File filePublicKey = context.getFileStreamPath(publicKeyPath);
            FileInputStream fis = context.openFileInput(publicKeyPath);
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            fis.read(encodedPublicKey);
            fis.close();

            String privateKeyPath = PREF_FILE + File.pathSeparator + "private.key";
            // Read Private Key.
            File filePrivateKey = context.getFileStreamPath(privateKeyPath);
            fis = context.openFileInput(privateKeyPath);
            byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
            fis.read(encodedPrivateKey);
            fis.close();

            // Generate KeyPair.
            KeyFactory keyFactory = KeyFactory.getInstance(KEYPAIR_ALGO);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                    encodedPublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                    encodedPrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            logWrapper.e(TAG, e);
        }
        return null;
    }

    protected void createNewKeys() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initializeKeystoreAndroidM();
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                initializeKeystore();
            } else {
                if (loadKeyPair() == null) {
                    KeyPairGenerator generator = KeyPairGenerator.getInstance(KEYPAIR_ALGO);
                    generator.initialize(2048);
                    KeyPair keyPair = generator.generateKeyPair();
                    saveKeyPair(keyPair);
                }
            }
        } catch (Exception e) {
            logWrapper.wtf(TAG, Log.getStackTraceString(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void initializeKeystore() throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // Create new key if needed
        if (!keyStore.containsAlias(keyAlias)) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(keyAlias)
                    .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEYPAIR_ALGO, ANDROID_KEY_STORE);
            generator.initialize(spec);
            generator.generateKeyPair();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void initializeKeystoreAndroidM() throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // Create new key if needed
        if (!keyStore.containsAlias(keyAlias)) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1).build());
            keyPairGenerator.generateKeyPair();
        }
    }


    @Override
    public String asymmetricEncrypt(String data) {
        try {
            Key publicKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
                publicKey = privateKeyEntry.getCertificate().getPublicKey();
            } else {
                publicKey = loadKeyPair().getPublic();
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
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
                privateKey = privateKeyEntry.getPrivateKey();

            } else {
                privateKey = loadKeyPair().getPrivate();
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
            return AESEncryption.symetricEncrypt(seed, data);
        } catch (Exception e) {
            logWrapper.w(TAG, e);
            return null;
        }
    }

    @Override
    public String symetricDecrypt(String seed, String encryptedData) {
        try {
            return AESEncryption.symetricDecrypt(seed, encryptedData);
        } catch (Exception e) {
            logWrapper.w(TAG, e);
            return null;
        }
    }


}
