package com.leroymerlin.pandroid.app.delegate.impl;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by florian on 10/02/16.
 */
public class ButterKnifeLifecycleDelegate extends SimpleLifecycleDelegate {
    private Unbinder unbinder;

    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        unbinder = ButterKnife.bind(target, view);
    }

    @Override
    public void onDestroyView(Object target) {
        super.onDestroyView(target);
        unbinder.unbind();
    }
}
