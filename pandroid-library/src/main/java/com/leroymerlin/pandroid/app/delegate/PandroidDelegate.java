package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.view.View;

import com.leroymerlin.pandroid.app.ResumeState;
import com.leroymerlin.pandroid.future.Cancellable;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by florian on 26/11/15.
 */
public class PandroidDelegate<T> extends SimpleLifecycleDelegate<T> implements
        CancellableActionDelegate.CancellableRegister, PandroidDelegateProvider {

    public static final String TAG = "PandroidLifecycleDelegate";

    protected WeakReference<T> targetRef;

    private List<Cancellable> cancellables = new ArrayList<>();
    private ArrayList<LifecycleDelegate> lifecycleDelegates = new ArrayList<>();

    public ResumeState getResumeState() {
        return resumeState;
    }

    @SuppressWarnings("unchecked")
    public void addLifecycleDelegate(@NonNull LifecycleDelegate lifecycleDelegate) {
        int index = lifecycleDelegates.size();
        while (index > 0) {
            if (lifecycleDelegates.get(index - 1).getPriority() <= lifecycleDelegate.getPriority()) {
                break;
            } else {
                index--;
            }
        }
        this.lifecycleDelegates.add(index, lifecycleDelegate);
        if (targetRef != null && targetRef.get() != null) {
            lifecycleDelegate.onInit(targetRef.get());
        }
    }

    @SuppressWarnings("unchecked")
    public boolean removeLifecycleDelegate(LifecycleDelegate lifecycleDelegate) {
        boolean removed = this.lifecycleDelegates.remove(lifecycleDelegate);
        if (targetRef != null && targetRef.get() != null && removed) {
            lifecycleDelegate.onRemove(targetRef.get());
        }
        return false;
    }

    public LifecycleDelegate getLifecycleDelegate(Class<? extends LifecycleDelegate> delegateType) {
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            if (lifecycleDelegate.getClass().isAssignableFrom(delegateType)) {
                return lifecycleDelegate;
            }
        }
        return null;
    }

    public List<LifecycleDelegate> getLifecycleDelegates() {
        return Collections.unmodifiableList(lifecycleDelegates);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onInit(T target) {
        super.onInit(target);
        this.targetRef = new WeakReference<T>(target);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            lifecycleDelegate.onInit(target);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreateView(T target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            lifecycleDelegate.onCreateView(target, view, savedInstanceState);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResume(T target) {
        super.onResume(target);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            lifecycleDelegate.onResume(target);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPause(T target) {
        super.onPause(target);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            lifecycleDelegate.onPause(target);
        }
        for (Cancellable delegate : cancellables) {
            delegate.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSaveView(T target, Bundle outState) {
        super.onSaveView(target, outState);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            lifecycleDelegate.onSaveView(target, outState);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDestroyView(T target) {
        super.onDestroyView(target);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            lifecycleDelegate.onDestroyView(target);
        }
    }

    @Override
    public void onRemove(T target) {
        super.onRemove(target);
        for (int i = lifecycleDelegates.size() - 1; i >= 0; i--) {
            LifecycleDelegate lifecycleDelegate = lifecycleDelegates.get(i);
            removeLifecycleDelegate(lifecycleDelegate);
        }
        this.targetRef = null;
    }

    @Override
    public void registerDelegate(Cancellable delegate) {
        this.cancellables.add(delegate);
        if (!viewExist) {
            delegate.cancel();
        }
    }

    @Override
    public boolean unregisterDelegate(Cancellable delegate) {
        return this.cancellables.remove(delegate);
    }

    @Override
    public PandroidDelegate getPandroidDelegate() {
        return this;
    }
}
