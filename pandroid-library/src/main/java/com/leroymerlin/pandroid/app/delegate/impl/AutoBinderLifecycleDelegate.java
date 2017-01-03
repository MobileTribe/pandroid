package com.leroymerlin.pandroid.app.delegate.impl;

import com.leroymerlin.pandroid.app.PandroidMapper;
import com.leroymerlin.pandroid.app.delegate.LifecycleDelegateAutoBinder;
import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

/**
 * Created by Mehdi on 07/11/2016.
 */

public class AutoBinderLifecycleDelegate extends SimpleLifecycleDelegate<Object> {

    @Override
    public void onInit(Object target) {
        super.onInit(target);
        for (LifecycleDelegateAutoBinder lifecycleDelegateAutoBinder : PandroidMapper.getInstance().getGeneratedInstances(LifecycleDelegateAutoBinder.class, target)) {
            lifecycleDelegateAutoBinder.bind();
        }
    }
}
