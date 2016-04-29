package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.ResumeState;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 26/11/15.
 */
public class PandroidDelegate extends SimpleLifecycleDelegate implements CancellableActionDelegate.ActionDelegateRegister {

    public static final String TAG = "PandroidLifecycleDelegate";
    private List<CancellableActionDelegate> delegates = new ArrayList<>();
    private List<LifecycleDelegate> lifecycleDelegates = new ArrayList<>();

    public ResumeState getResumeState() {
        return resumeState;
    }

    public void addLifecycleDelegate(LifecycleDelegate lifecycleDelegate) {
        this.lifecycleDelegates.add(lifecycleDelegate);
    }

    public boolean removeLifecycleDelegate(LifecycleDelegate lifecycleDelegate) {
        return this.lifecycleDelegates.remove(lifecycleDelegate);
    }

    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onCreateView(target, view, savedInstanceState);
        }
    }

    @Override
    public void onResume(Object target) {
        super.onResume(target);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onResume(target);
        }
    }

    @Override
    public void onPause(Object target) {
        super.onPause(target);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onPause(target);
        }
    }

    @Override
    public void onSaveView(Object target, Bundle outState) {
        super.onSaveView(target, outState);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onSaveView(target, outState);
        }
    }

    @Override
    public void onDestroyView(Object target) {
        super.onDestroyView(target);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onDestroyView(target);
        }
        for (CancellableActionDelegate delegate : delegates) {
            delegate.cancel();
        }
    }


    @Override
    public void registerDelegate(CancellableActionDelegate delegate) {
        this.delegates.add(delegate);
        if (!viewCreated) {
            delegate.cancel();
        }
    }

    @Override
    public boolean unregisterDelegate(CancellableActionDelegate delegate) {
        return this.delegates.remove(delegate);
    }

}
