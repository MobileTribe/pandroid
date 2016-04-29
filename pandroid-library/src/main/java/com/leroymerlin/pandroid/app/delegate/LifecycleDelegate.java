package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.view.View;

/**
 * Created by florian on 10/02/16.
 */
public interface LifecycleDelegate {


    void onCreateView(Object target, View view, Bundle savedInstanceState);

    void onResume(Object target);

    void onPause(Object object);

    void onSaveView(Object target, Bundle outState);

    void onDestroyView(Object target);
}
