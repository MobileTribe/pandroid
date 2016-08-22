package com.leroymerlin.pandroid.app.delegate;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.log.LogcatLogger;

/**
 * Created by florian on 11/07/2016.
 */

public class DaggerDelegate extends SimpleLifecycleDelegate<Object> {


    private static final String TAG = "DaggerDelegate";

    @Override
    public void onInit(Object target) {
        Context context = null;
        if (target instanceof Activity) {
            context = (Context) target;
        } else if (target instanceof Fragment) {
            context = ((Fragment) target).getActivity();
        }
        if (context != null) {
            PandroidApplication pandroidApplication = PandroidApplication.get(context);
            pandroidApplication.inject(target);
        } else {
            String className = "null";
            if (target != null) {
                target.getClass().getSimpleName();
            }
            LogcatLogger.getInstance().w(TAG, "Can't inject in object of type : " + className);
        }
    }
}
