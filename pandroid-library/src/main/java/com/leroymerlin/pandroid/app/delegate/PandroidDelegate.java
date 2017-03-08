package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.leroymerlin.pandroid.app.ResumeState;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 26/11/15.
 */
public class PandroidDelegate<T> extends SimpleLifecycleDelegate<T> implements
        CancellableActionDelegate.ActionDelegateRegister {

    public static final String TAG = "PandroidLifecycleDelegate";
    private List<CancellableActionDelegate> delegates = new ArrayList<>();
    private List<LifecycleDelegate> lifecycleDelegates = new ArrayList<>();

    public ResumeState getResumeState() {
        return resumeState;
    }

    public void addLifecycleDelegate(@NonNull LifecycleDelegate lifecycleDelegate) {
        this.lifecycleDelegates.add(lifecycleDelegate);
    }

    public boolean removeLifecycleDelegate(LifecycleDelegate lifecycleDelegate) {
        return this.lifecycleDelegates.remove(lifecycleDelegate);
    }

    public LifecycleDelegate getLifecycleDelegate(Class<? extends LifecycleDelegate> delegateType) {
        for (LifecycleDelegate delegate : lifecycleDelegates) {
            if (delegate.getClass().isAssignableFrom(delegateType)) {
                return delegate;
            }
        }
        return null;
    }

    public List<CancellableActionDelegate> getLifecycleDelegates() {
        return delegates;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onInit(T target) {
        super.onInit(target);
        for (int i = 0; i < lifecycleDelegates.size(); i++) {
            lifecycleDelegates.get(i).onInit(target);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreateView(T target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onCreateView(target, view, savedInstanceState);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResume(T target) {
        super.onResume(target);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onResume(target);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPause(T target) {
        super.onPause(target);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onPause(target);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSaveView(T target, Bundle outState) {
        super.onSaveView(target, outState);
        for (LifecycleDelegate lifecycleDelegate : lifecycleDelegates) {
            lifecycleDelegate.onSaveView(target, outState);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDestroyView(T target) {
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
        if (!viewExist) {
            delegate.cancel();
        }
    }

    @Override
    public boolean unregisterDelegate(CancellableActionDelegate delegate) {
        return this.delegates.remove(delegate);
    }

}
