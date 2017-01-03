package com.leroymerlin.pandroid.app.delegate.impl;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.PandroidMapper;
import com.leroymerlin.pandroid.app.delegate.LifecycleDelegate;
import com.leroymerlin.pandroid.event.AbstractReceiver;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.ReceiversProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 10/02/16.
 */
public class EventBusLifecycleDelegate implements LifecycleDelegate<Object> {

    private static final String TAG = "EventBusLifecycleDelegate";
    protected final EventBusManager eventBusManager;
    protected List<EventBusManager.EventBusReceiver> receivers = new ArrayList<>();

    public EventBusLifecycleDelegate(EventBusManager eventBusManager) {
        this.eventBusManager = eventBusManager;
    }

    @Override
    public void onInit(Object target) {

    }

    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {

    }

    @Override
    public void onResume(Object target) {
        if (target instanceof ReceiversProvider) {
            List<EventBusManager.EventBusReceiver> receivers = ((ReceiversProvider) target).getReceivers();
            boolean isActivity = target instanceof Activity;
            boolean isFragment = target instanceof Fragment;
            if (isActivity || isFragment) {

                Context context = null;
                FragmentManager manager = null;
                if (isActivity) {
                    context = ((Activity) target).getApplication();
                    manager = ((Activity) target).getFragmentManager();
                } else { //if(isFragment){
                    context = ((Fragment) target).getActivity().getApplication();
                    manager = ((Fragment) target).getChildFragmentManager();
                }

                for (EventBusManager.EventBusReceiver receiver : receivers) {
                    if (receiver instanceof AbstractReceiver) {
                        ((AbstractReceiver) receiver).attach(context, target, manager);
                    }
                }

            }
            this.receivers.addAll(receivers);
        }
        List<ReceiversProvider> receiversProviders = PandroidMapper.getInstance().getGeneratedInstances(ReceiversProvider.class, target);
        for (ReceiversProvider receiversProvider : receiversProviders) {
            receivers.addAll(receiversProvider.getReceivers());
        }
        eventBusManager.registerReceivers(receivers);
    }

    @Override
    public void onPause(Object object) {
        eventBusManager.unregisterReceivers(receivers);
        for (EventBusManager.EventBusReceiver receiver : receivers) {
            if (receiver instanceof AbstractReceiver) {
                ((AbstractReceiver) receiver).detach();
            }
        }
        receivers.clear();
    }

    @Override
    public void onSaveView(Object target, Bundle outState) {

    }

    @Override
    public void onDestroyView(Object target) {

    }
}
