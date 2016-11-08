package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.ResumeState;

/**
 * Created by florian on 10/02/16.
 */
public class SimpleLifecycleDelegate<T> implements LifecycleDelegate<T> {


    protected Bundle savedInstanceState;
    protected ResumeState resumeState;
    protected boolean viewExist;
    protected boolean newInstance;

    @Override
    public void onInit(T target) {
        newInstance = true;
    }

    @Override
    public void onCreateView(T target, View view, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        viewExist = true;
    }

    @Override
    public void onResume(T target) {
        resumeState = ResumeState.VIEW_RESTORED;
        if (savedInstanceState == null && newInstance) { //firstStart
            resumeState = ResumeState.FIRST_START;
        } else if (newInstance) { //rotation
            resumeState = ResumeState.ROTATION;
        }
        newInstance = false;
    }

    @Override
    public void onPause(T object) {

    }

    @Override
    public void onSaveView(T target, Bundle outState) {
    }

    @Override
    public void onDestroyView(T target) {
        viewExist = false;
    }

}
