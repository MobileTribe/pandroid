package com.leroymerlin.pandroid.mvp;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.view.View;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;
import com.leroymerlin.pandroid.log.LogcatLogger;

import java.lang.ref.WeakReference;

/**
 * Created by Mehdi on 07/11/2016.
 */

public class Presenter<T> extends SimpleLifecycleDelegate<T> implements CancellableActionDelegate
        .ActionDelegateRegister {

    private static final String TAG = Presenter.class.getSimpleName();

    private WeakReference<Application> mApplication;
    private WeakReference<T> mView;
    private final PandroidDelegate mDelegate;

    public Presenter(Application application) {
        mApplication = new WeakReference<>(application);
        mDelegate = new PandroidDelegate();
    }

    @Override
    public void onInit(T target) {
        super.onInit(target);
        mView = new WeakReference<>(target);
        final Application activity = mApplication.get();
        if (activity != null) {
            try {
                PandroidApplication.get(activity).inject(this);
            } catch (Exception e) {
                LogcatLogger.getInstance().wtf(TAG, e);
            }
        } else {
            LogcatLogger.getInstance().wtf(TAG, "Activity not found onInit(T target)");
        }
        mDelegate.onInit(target);
    }

    @Override
    public void onCreateView(T target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        mDelegate.onCreateView(target, view, savedInstanceState);
    }

    @Override
    public void onResume(T target) {
        super.onResume(target);
        mDelegate.onResume(target);
    }

    @Override
    public void onPause(T object) {
        super.onPause(object);
        mDelegate.onPause(object);
    }

    @Override
    public void onSaveView(T target, Bundle outState) {
        super.onSaveView(target, outState);
        mDelegate.onSaveView(target, outState);
    }

    @Override
    public void onDestroyView(T target) {
        super.onDestroyView(target);
        mDelegate.onDestroyView(target);
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

}
