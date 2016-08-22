package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.view.View;

/**
 * Created by florian on 10/02/16.
 */
public interface LifecycleDelegate<T> {

    void onInit(T target);

    void onCreateView(T target, View view, Bundle savedInstanceState);

    void onResume(T target);

    void onPause(T target);

    void onSaveView(T target, Bundle outState);

    void onDestroyView(T target);
}
