package com.leroymerlin.pandroid.mvp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.app.PandroidConfig;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.AutoBinderLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.rx.RxLifecycleDelegate;

import java.lang.ref.WeakReference;

/**
 * Created by Mehdi on 07/11/2016.
 */

@SuppressWarnings("unchecked")
@RxWrapper
public class Presenter<T> extends PandroidDelegate<T> {

    protected WeakReference<T> targetView;

    public Presenter() {
    }

    protected void initNestedLifecycleDelegate() {
        if (PandroidConfig.isLibraryEnable("rxandroid")) {
            addLifecycleDelegate(new RxLifecycleDelegate());
        }
        addLifecycleDelegate(new AutoBinderLifecycleDelegate());
    }

    @CallSuper
    @Override
    public void onInit(T target) {
        super.onInit((T) this);
        initNestedLifecycleDelegate();
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
        targetView = new WeakReference<>(target);
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
        targetView.clear();
        super.onDestroyView((T) this);
    }

    @Override
    public void onRemove(T target) {
        targetView = null;
        super.onRemove((T) this);
    }

    @Nullable
    public T getView() {
        if (targetView != null) {
            return targetView.get();
        }
        return null;
    }

}
