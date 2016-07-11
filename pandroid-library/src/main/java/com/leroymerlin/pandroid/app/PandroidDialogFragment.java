package com.leroymerlin.pandroid.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.event.ReceiversProvider;
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
public class PandroidDialogFragment<T extends FragmentOpener> extends DialogFragment implements CancellableActionDelegate.ActionDelegateRegister, ReceiversProvider {

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
        PandroidApplication pandroidApplication = PandroidApplication.get(getActivity());
        //initialize PandroidDelegate
        pandroidDelegate = pandroidApplication.createBasePandroidDelegate();
        pandroidDelegate.onInit(this);
        if (getArguments() != null && getArguments().containsKey(FragmentOpener.ARG_OPENER)) {
            mOpener = (T) getArguments().get(FragmentOpener.ARG_OPENER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pandroidDelegate.onCreateView(this, view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        pandroidDelegate.onResume(this);
        onResume(pandroidDelegate.getResumeState());
    }

    public void onResume(ResumeState state) {
        logWrapper.i(getClass().getSimpleName(), "resume state: " + state);
    }

    @Override
    public void onPause() {
        super.onPause();
        pandroidDelegate.onPause(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        pandroidDelegate.onSaveView(this, outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pandroidDelegate.onDestroyView(this);
    }

    public void startFragment(Class<? extends PandroidDialogFragment> fragmentClass) {
        sendEventSync(new FragmentOpener(fragmentClass));
    }

    public void sendEvent(Object event) {
        eventBusManager.send(event);
    }

    public void sendEventSync(Object event) {
        eventBusManager.sendSync(event);
    }

    @Override
    public void registerDelegate(CancellableActionDelegate delegate) {
        pandroidDelegate.registerDelegate(delegate);
    }

    @Override
    public boolean unregisterDelegate(CancellableActionDelegate delegate) {
        return pandroidDelegate.unregisterDelegate(delegate);
    }

    @Override
    public List<EventBusManager.EventBusReceiver> getReceivers() {
        return new ArrayList<>();
    }
}
