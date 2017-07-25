package com.leroymerlin.pandroid.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegateProvider;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.leroymerlin.pandroid.future.Cancellable;
import com.leroymerlin.pandroid.future.CancellableActionDelegate;
import com.leroymerlin.pandroid.log.LogWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by florian on 05/11/14.
 * <p/>
 * PandroidFragment is a Fragment that simplify the fragment cycle of life by introducing onResume(ResumeState) method.
 * If static field TAG is set PandroidFragment inject Broadcast receiver himself
 */
@RxWrapper
public class PandroidFragment<T extends FragmentOpener> extends Fragment implements CancellableActionDelegate.CancellableRegister, ReceiversProvider, PandroidDelegateProvider {

    /**
     * Default logger
     */
    @Inject
    protected LogWrapper logWrapper;
    /**
     * Handle App Event
     */
    @Inject
    protected EventBusManager eventBusManager;

    protected T mOpener;

    protected PandroidDelegate pandroidDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize PandroidDelegate
        pandroidDelegate = createDelegate();
        pandroidDelegate.onInit(this);
        if (getArguments() != null && getArguments().containsKey(FragmentOpener.ARG_OPENER)) {
            mOpener = (T) getArguments().get(FragmentOpener.ARG_OPENER);
        }

    }

    @Override
    public PandroidDelegate getPandroidDelegate() {
        return pandroidDelegate;
    }

    protected PandroidDelegate createDelegate() {
        PandroidDelegateProvider pandroidApplication = (PandroidDelegateProvider) getActivity().getApplicationContext();
        //initialize Base PandroidDelegate
        return pandroidApplication.getPandroidDelegate();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pandroidDelegate.onCreateView(this, view, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        pandroidDelegate.onResume(this);
        onResume(pandroidDelegate.getResumeState());
    }

    public void onResume(ResumeState state) {
        logWrapper.i(getClass().getSimpleName(), "resume state: " + state);
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();
        pandroidDelegate.onPause(this);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pandroidDelegate.onDestroyView(this);

    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        pandroidDelegate.onSaveView(this, outState);
    }

    public void startFragment(Class<? extends Fragment> fragmentClass) {
        sendEventSync(new FragmentOpener(fragmentClass));
    }

    public void sendEvent(Object event) {
        eventBusManager.send(event);
    }

    public void sendEventSync(Object event) {
        eventBusManager.sendSync(event);
    }

    @Override
    public void registerDelegate(Cancellable delegate) {
        pandroidDelegate.registerDelegate(delegate);
    }

    @Override
    public boolean unregisterDelegate(Cancellable delegate) {
        return pandroidDelegate.unregisterDelegate(delegate);
    }

    @Override
    public List<EventBusManager.EventBusReceiver> getReceivers() {
        return new ArrayList<>();
    }
}
