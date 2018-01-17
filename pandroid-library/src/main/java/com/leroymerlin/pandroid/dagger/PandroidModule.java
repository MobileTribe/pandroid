package com.leroymerlin.pandroid.dagger;

import android.app.Application;
import android.content.Context;

import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.EventBusManagerImpl;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by mehdi on 30/11/2015.
 */
@Module
public class PandroidModule {

    private static final String TAG = "PandroidModule";


    protected final Application mApplication;


    public PandroidModule(Application application) {
        mApplication = application;
    }


    @Provides
    protected Application provideApplication() {
        return mApplication;
    }

    @Provides
    protected Context provideContext() {
        return mApplication;
    }

    @Provides
    protected LogWrapper provideLogWrapper() {
        return PandroidLogger.getInstance();
    }

    @Provides
    protected EventBusManager provideEventBusManager() {
        return EventBusManagerImpl.getMainInstance();
    }

    @Provides
    @Singleton
    protected List<TrustManager> provideTrustManagers() {
        return getTrustManagers();
    }

    protected List<TrustManager>  getTrustManagers() {
        return new ArrayList<>();
    }


    @Provides
    @Singleton
    protected List<KeyManager> provideKeyManagers() {
        return getKeyManagers();
    }

    protected List<KeyManager> getKeyManagers() {
        return new ArrayList<>();
    }


    @Provides
    @Singleton
    protected SSLContext provideSslContext(List<KeyManager> keyManagers, List<TrustManager> trustManagers, LogWrapper logWrapper) {
        return getSslContext(keyManagers, trustManagers, logWrapper);
    }

    protected SSLContext getSslContext(List<KeyManager> keyManagers, List<TrustManager> trustManagers, LogWrapper logWrapper) {
        KeyManager[] keyManagersArray = null;
        if (keyManagers != null && keyManagers.size() > 0) {
            keyManagersArray = new KeyManager[keyManagers.size()];
            keyManagers.toArray(keyManagersArray);
        }
        TrustManager[] trustManagersArray = null;
        if (trustManagers != null && trustManagers.size() > 0) {
            trustManagersArray = new TrustManager[trustManagers.size()];
            trustManagers.toArray(trustManagersArray);
        }
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagersArray, trustManagersArray, null);
            return sslContext;
        } catch (Exception e) {
            logWrapper.e(TAG, e);
        }
        return null;
    }

    @Provides
    protected OkHttpClient.Builder provideOkHttpClient(SSLContext sslContext) {
        return getOkHttpClientBuilder(sslContext);
    }

    protected OkHttpClient.Builder getOkHttpClientBuilder(SSLContext sslContext) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(socketFactory, getTrustManager());
        return builder;
    }

    protected X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            return null;
        }
    }

}
