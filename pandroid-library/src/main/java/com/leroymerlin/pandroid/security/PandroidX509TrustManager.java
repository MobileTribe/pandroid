package com.leroymerlin.pandroid.security;

import android.content.Context;

import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * Created by adrien on 9/01/15.
 * http://nelenkov.blogspot.fr/2011/12/using-custom-certificate-trust-store-on.html
 * <p/>
 * This class provide a custom trustmanager which is validating the default certificate and adeo
 */
public class PandroidX509TrustManager implements X509TrustManager {


    private static final String TAG = "PandroidX509TrustManager";

    private KeyStore keyStore;
    private final Context context;
    private TrustManagerFactory trustMgrFactory;

    LogWrapper logger = PandroidLogger.getInstance();


    private X509TrustManager defaultTrustManager;


    public PandroidX509TrustManager(Context context) {
        this.context = context;

        try {

            //load the defaults certificates
            trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustMgrFactory.init((KeyStore) null);
            TrustManager trustManagers[] = trustMgrFactory.getTrustManagers();
            for (int i = 0; i < trustManagers.length; i++) {
                if (trustManagers[i] instanceof X509TrustManager) {
                    defaultTrustManager = (X509TrustManager) trustManagers[i];
                }
            }

            this.keyStore = initNewKeyStore(context);
            trustMgrFactory.init(keyStore);
        } catch (Exception e) {
            logger.wtf(TAG, e);
        }


    }

    private static KeyStore initNewKeyStore(Context context) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

//        // read and add certificate authority
//        for (int id : certificates) {
//            Certificate cert = readCert(context, id);
//            keyStore.setCertificateEntry("ca" + id, cert);
//        }
        return keyStore;

    }


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException ce) {
            TrustManager[] trustManagers = trustMgrFactory.getTrustManagers();
            for (int i = 0; i < trustManagers.length; i++) {
                if (trustManagers[i] instanceof X509TrustManager) {
                    ((X509TrustManager) trustManagers[i]).checkServerTrusted(chain, authType);
                    return;
                }
            }
            throw new CertificateException();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }


    public void addCertificate(String name, Certificate certificate) {
        try {
            keyStore.setCertificateEntry(name, certificate);
        } catch (KeyStoreException e) {
            logger.e(TAG, e);
        }
    }

    public void addCertificate(String name, int certResourceId) {
        try {
            addCertificate(name, readCert(certResourceId));
        } catch (CertificateException e) {
            logger.e(TAG, e);
        } catch (IOException e) {
            logger.e(TAG, e);
        }
    }

    private Certificate readCert(int certResourceId) throws CertificateException, IOException {
        // read certificate resource
        InputStream caInput = context.getResources().openRawResource(certResourceId);

        Certificate ca;
        try {
            // generate a certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        return ca;
    }
}
