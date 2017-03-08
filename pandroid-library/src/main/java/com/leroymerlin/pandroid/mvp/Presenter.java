package com.leroymerlin.pandroid.mvp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.leroymerlin.pandroid.app.delegate.LifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;

import java.lang.ref.WeakReference;

/**
 * Created by Mehdi on 07/11/2016.
 */

public class Presenter<T> extends SimpleLifecycleDelegate<T> implements CancellableActionDelegate
        .ActionDelegateRegister {

    private static final String TAG = Presenter.class.getSimpleName();

    private WeakReference<T> mView;
    private final PandroidDelegate mDelegate;

    public Presenter() {
        mDelegate = new PandroidDelegate();
    }

    @CallSuper
    @Override
    public void onInit(T target) {
        super.onInit(target);
        mView = new WeakReference<>(target);
        mDelegate.onInit(this);
    }

    @Nullable
    protected Context getContext() {
        T view = mView.get();
        if (view instanceof Context) {
            return (Context) view;
        } else if (view instanceof Fragment) {
            return ((Fragment) view).getActivity();
        } else {
            return null;
        }
    }

    @Override
    public void onCreateView(T target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        mDelegate.onCreateView(this, view, savedInstanceState);
    }

    @Override
    public void onResume(T target) {
        super.onResume(target);
        mDelegate.onResume(this);
    }

    @Override
    public void onPause(T object) {
        super.onPause(object);
        mDelegate.onPause(this);
    }

    @Override
    public void onSaveView(T target, Bundle outState) {
        super.onSaveView(target, outState);
        mDelegate.onSaveView(this, outState);
    }

    @Override
    public void onDestroyView(T target) {
        super.onDestroyView(target);
        mDelegate.onDestroyView(this);
    }

    @Nullable
    @CheckResult
    public T getView() {
        return mView.get();
    }

    @Override
    public void registerDelegate(CancellableActionDelegate delegate) {
        mDelegate.registerDelegate(delegate);
    }

    @Override
    public boolean unregisterDelegate(CancellableActionDelegate delegate) {
        return mDelegate.unregisterDelegate(delegate);
    }

    protected void addLifecycleDelegate(@NonNull LifecycleDelegate lifecycleDelegate) {
        mDelegate.addLifecycleDelegate(lifecycleDelegate);
    }

    protected boolean removeLifecycleDelegate(LifecycleDelegate lifecycleDelegate) {
        return mDelegate.removeLifecycleDelegate(lifecycleDelegate);
    }

}
