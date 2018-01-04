package com.leroymerlin.pandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

/**
 * Created by florian on 03/01/2018.
 * <p>
 * If your AppCompatActivity implement this interface you can call
 * PandroidViewFactory.installPandroidViewFactory(this) before super.onCreate(savedInstanceState)
 * to add custom LayoutInflater factories
 */
public interface PandroidFactoryProvider {

    List<LayoutInflater.Factory2> getLayoutInflaterFactories();
}
