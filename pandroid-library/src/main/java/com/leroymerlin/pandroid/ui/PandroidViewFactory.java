package com.leroymerlin.pandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.leroymerlin.pandroid.log.LogcatLogger;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

/**
 * Created by florian on 03/01/2018.
 */

public class PandroidViewFactory implements LayoutInflater.Factory2 {


    private static final String TAG = "PandroidLayoutInflater";

    private List<LayoutInflater.Factory2> factories;
    private AppCompatDelegate appDelegate;

    private PandroidViewFactory(AppCompatDelegate delegate, List<LayoutInflater.Factory2> factories) {
        this.factories = factories;
        this.appDelegate = delegate;
    }

    public static void installPandroidViewFactory(AppCompatActivity compatActivity) {
        List<LayoutInflater.Factory2> factories = new ArrayList<>();
        if (compatActivity instanceof PandroidFactoryProvider) {
            addProviderFactories((PandroidFactoryProvider) compatActivity, factories);
        }
        if (compatActivity.getApplication() instanceof PandroidFactoryProvider) {
            addProviderFactories((PandroidFactoryProvider) compatActivity.getApplication(), factories);
        }
        if (!factories.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(compatActivity);
            if (inflater.getFactory2() == null) {
                PandroidViewFactory factory = new PandroidViewFactory(compatActivity.getDelegate(), factories);
                LayoutInflaterCompat.setFactory2(inflater, factory);
            } else {
                LogcatLogger.getInstance().w(TAG, "can't set layout inflater factory");
            }
        } else {
            LogcatLogger.getInstance().w(TAG, "Your activity or application should implement PandroidFactoryProvider to install PandroidLayoutInflaterFactory");
        }

    }

    private static void addProviderFactories(PandroidFactoryProvider compatActivity, List<LayoutInflater.Factory2> factories) {
        List<LayoutInflater.Factory2> activityFactories = compatActivity.getLayoutInflaterFactories();
        if (activityFactories != null) {
            factories.addAll(activityFactories);
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        for (LayoutInflater.Factory2 factory2 : factories) {
            View result = factory2.onCreateView(parent, name, context, attrs);
            if (result != null)
                return result;
        }
        return appDelegate.createView(parent, name, context, attrs);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return onCreateView(null, name, context, attrs);
    }
}
