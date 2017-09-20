package com.leroymerlin.pandroid.mvp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.view.View;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.AutoBinderLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.rx.RxLifecycleDelegate;

import java.lang.ref.WeakReference;

/**
 * Created by Mehdi on 07/11/2016.
 */

@RxWrapper
public class Presenter<T> extends PandroidDelegate<T> {

    private static final String TAG = "Presenter";

    public Presenter() {
        addLifecycleDelegate(new RxLifecycleDelegate());
        addLifecycleDelegate(new AutoBinderLifecycleDelegate());
    }

    @CallSuper
    @Override
    public void onInit(T target) {
        super.onInit((T) this);
    }

    @Nullable
    protected Context getContext() {
        T view = getView();
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
        super.onCreateView((T) this, view, savedInstanceState);
    }

    @Override
    public void onResume(T target) {
        super.onResume((T) this);
    }

    @Override
    public void onPause(T object) {
        super.onPause((T) this);
    }

    @Override
    public void onSaveView(T target, Bundle outState) {
        super.onSaveView((T) this, outState);
    }

    @Override
    public void onDestroyView(T target) {
        super.onDestroyView((T) this);
    }

    @Nullable
    @CheckResult
    public T getView() {
        return targetRef != null ? targetRef.get() : null;
    }

}
