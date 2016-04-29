package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.ResumeState;

/**
 * Created by florian on 10/02/16.
 */
public class SimpleLifecycleDelegate implements LifecycleDelegate {


    protected Bundle savedInstanceState;
    protected boolean onCreate;
    protected ResumeState resumeState;
    protected boolean viewCreated;

    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        onCreate = true;
        viewCreated = true;


    }

    @Override
    public void onResume(Object target) {
        resumeState = ResumeState.VIEW_RESTORED;
        if (onCreate && savedInstanceState == null) { //firstStart
            resumeState = ResumeState.FIRST_START;
        } else if (onCreate) { //rotation
            resumeState = ResumeState.ROTATION;
        }
        onCreate = false;
    }

    @Override
    public void onPause(Object object) {

    }

    @Override
    public void onSaveView(Object target, Bundle outState) {
    }

    @Override
    public void onDestroyView(Object target) {
        viewCreated = false;
    }

}
