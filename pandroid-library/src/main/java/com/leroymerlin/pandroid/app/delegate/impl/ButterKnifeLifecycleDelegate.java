package com.leroymerlin.pandroid.app.delegate.impl;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

import butterknife.ButterKnife;


/**
 * Created by florian on 10/02/16.
 */
public class ButterKnifeLifecycleDelegate extends SimpleLifecycleDelegate {
    @Override
    public void onCreateView(Object target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        ButterKnife.bind(target, view);
    }
}
