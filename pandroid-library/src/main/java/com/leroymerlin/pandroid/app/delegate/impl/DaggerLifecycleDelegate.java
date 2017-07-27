package com.leroymerlin.pandroid.app.delegate.impl;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegateProvider;
import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;
import com.leroymerlin.pandroid.dagger.PandroidDaggerProvider;
import com.leroymerlin.pandroid.event.opener.OpenerReceiverProvider;
import com.leroymerlin.pandroid.log.LogcatLogger;

/**
 * Created by florian on 11/07/2016.
 */

public class DaggerLifecycleDelegate extends SimpleLifecycleDelegate<Object> {


    private static final String TAG = "DaggerDelegate";

    @Override
    public void onInit(Object target) {
        if (target instanceof OpenerReceiverProvider) {
            Context appContext = ((OpenerReceiverProvider) target).provideActivity().getApplicationContext();
            if (appContext instanceof PandroidDaggerProvider) {
                ((PandroidDaggerProvider) appContext).inject(target);
            } else {
                LogcatLogger.getInstance().w(TAG, "Can't inject. Your application should implement PandroidDaggerProvider");
            }
        } else {
            String className = "null";
            if (target != null) {
                className = target.getClass().getSimpleName();
            }
            LogcatLogger.getInstance().w(TAG, "Can't inject in object of type : " + className);
        }
    }
}
