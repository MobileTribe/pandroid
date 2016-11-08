package com.leroymerlin.pandroid.demo.main.mvp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

/**
 * Created by Mehdi on 08/11/2016.
 */
public class LoggerLifeCycleDelegate extends SimpleLifecycleDelegate<PresenterFragment> {

    private static final String TAG = LoggerLifeCycleDelegate.class.getSimpleName();

    @Override
    public void onInit(PresenterFragment target) {
        super.onInit(target);
        Log.d(TAG, "onInit");
    }

    @Override
    public void onCreateView(PresenterFragment target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        Log.d(TAG, "onCreateView");
    }

    @Override
    public void onResume(PresenterFragment target) {
        super.onResume(target);
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause(PresenterFragment object) {
        super.onPause(object);
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroyView(PresenterFragment target) {
        super.onDestroyView(target);
        Log.d(TAG, "onDestroyView");
    }

}
