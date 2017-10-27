package com.leroymerlin.pandroid.app.delegate;

import android.os.Bundle;
import android.view.View;

/**
 * Created by florian on 10/02/16.
 */
public interface LifecycleDelegate<T> {

    int LOW_PRIORITY = 0;
    int DEFAULT_PRIORITY = LOW_PRIORITY + 10;
    int HIGH_PRIORITY = DEFAULT_PRIORITY + 10;

    /**
     * Called automatically when a delegate is attach on a PandroidDelegate.
     *
     * @param target on witch the delegate is attached
     */
    void onInit(T target);

    void onCreateView(T target, View view, Bundle savedInstanceState);

    void onResume(T target);

    void onPause(T target);

    void onSaveView(T target, Bundle outState);

    void onDestroyView(T target);

    /**
     * Called when a delegate is removed from a PandroidDelegate
     * Be careful this method won't be called if you delegate is destroyed with the target.
     *
     * @param target on witch the delegate is attached
     */
    void onRemove(T target);


    /**
     * Highest priority delegate will be called first in the PandroidDelegate/Presenter.
     * Use this property to order your delegate.
     * It could be useful if some delegate need dependency injection or view binding
     *
     * @return priority value. The biggest is a priority
     */
    int getPriority();
}
