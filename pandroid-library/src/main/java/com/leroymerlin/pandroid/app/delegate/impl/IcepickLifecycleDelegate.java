package com.leroymerlin.pandroid.app.delegate.impl;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

import icepick.Icepick;

/**
 * Created by florian on 10/02/16.
 */
//tag::LifecycleDelegate[]
public class IcepickLifecycleDelegate extends SimpleLifecycleDelegate<Object> {

    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        Icepick.restoreInstanceState(target, savedInstanceState);
    }

    @Override
    public void onSaveView(Object target, Bundle outState) {
        if (viewExist)
            Icepick.saveInstanceState(target, outState);
    }
}
//end::LifecycleDelegate[]
