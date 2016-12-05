package com.leroymerlin.pandroid.mvvm;

import android.os.Bundle;
import android.view.View;

import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

/**
 * Created by florian on 29/09/2016.
 */
public class ViewModelDelegate<T> extends SimpleLifecycleDelegate<T> {

    protected T contract;

    @Override
    public void onCreateView(T target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        if (contract == null)
            contract = target;
    }

    @Override
    public void onDestroyView(T target) {
        super.onDestroyView(target);
        contract = null;
    }
}
