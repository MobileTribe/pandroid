package com.leroymerlin.pandroid.demo;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.AnalyticsReceiver;
import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.dagger.BaseComponent;
import com.leroymerlin.pandroid.dagger.PandroidModule;
import com.leroymerlin.pandroid.demo.main.MainActivity;
import com.leroymerlin.pandroid.demo.main.opener.OpenerActivity;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.opener.ActivityEventReceiver;
import com.leroymerlin.pandroid.event.opener.OpenerEventReceiver;
import com.leroymerlin.pandroid.security.PandroidX509TrustManager;

import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

/**
 * Created by florian on 04/01/16.
 */
public class DemoApplication extends PandroidApplication {

    //tag::createBaseComponent[]
    @Override
    protected BaseComponent createBaseComponent() {
        return DaggerDemoComponent.builder()
                .demoModule(new DemoModule())
                .pandroidModule(new PandroidModule(this) {
                    //tag::certificat[]
                    @Override
                    protected List<TrustManager> getTrustManagers() {
                        List<TrustManager> keyManagers = super.getTrustManagers();
                        PandroidX509TrustManager trustManager = new PandroidX509TrustManager(DemoApplication.this);
                        trustManager.addCertificate("leroymerlin.com", R.raw.lmfr_cert);// add certificate for your application
                        keyManagers.add(trustManager);
                        return keyManagers;
                    }

                    @Override
                    protected List<KeyManager> getKeyManagers() {
                        return super.getKeyManagers(); // here you can add you own key manager
                    }
                    //end::certificat[]
                })
                .build();
    }
    //end::createBaseComponent[]


    //tag::ActivityOpener[]
    /**
     * create list of receiver inject in Activity
     * You can override this method to inject activity receiver easily
     *
     * @return list of activity receivers inject in activity
     */
    @Override
    protected List<ActivityEventReceiver> createBaseActivityReceivers() {
        List<ActivityEventReceiver> receivers = super.createBaseActivityReceivers();
        receivers.add(new ActivityEventReceiver()
                .overrideAnimation(new int[]{R.anim.fade_in, R.anim.fade_out})
                .addActivity(OpenerActivity.class));
        receivers.add(new ActivityEventReceiver()
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addActivity(MainActivity.class));
        return receivers;
    }
    //end::ActivityOpener[]

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
