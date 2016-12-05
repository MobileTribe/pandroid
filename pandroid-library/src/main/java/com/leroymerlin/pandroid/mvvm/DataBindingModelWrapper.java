package com.leroymerlin.pandroid.mvvm;

import android.databinding.CallbackRegistry;
import android.databinding.ObservableMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by florian on 30/09/2016.
 */

public abstract class DataBindingModelWrapper implements ObservableMap<String, Object> {

    private transient MapChangeRegistry mListeners;

    @Override
    public void addOnMapChangedCallback(
            OnMapChangedCallback<? extends ObservableMap<String, Object>, String, Object> listener) {
        if (mListeners == null) {
            mListeners = new MapChangeRegistry();
        }
        mListeners.add(listener);
    }

    @Override
    public void removeOnMapChangedCallback(
            OnMapChangedCallback<? extends ObservableMap<String, Object>, String, Object> listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
    }

    protected void notifyChange(Object key) {
        if (mListeners != null) {
            mListeners.notifyCallbacks(this, 0, key);
        }
    }

    private static class MapChangeRegistry
            extends CallbackRegistry<OnMapChangedCallback, ObservableMap, Object> {

        private static NotifierCallback<ObservableMap.OnMapChangedCallback, ObservableMap, Object> NOTIFIER_CALLBACK =
                new NotifierCallback<ObservableMap.OnMapChangedCallback, ObservableMap, Object>() {
                    @Override
                    public void onNotifyCallback(ObservableMap.OnMapChangedCallback callback, ObservableMap sender,
                                                 int arg, Object arg2) {
                        callback.onMapChanged(sender, arg2);
                    }
                };

        public MapChangeRegistry() {
            super(NOTIFIER_CALLBACK);
        }

        /**
         * Notifies registered callbacks that an element has been added, removed, or changed.
         *
         * @param sender The map that has changed.
         * @param key    The key of the element that changed.
         */
        public void notifyChange(ObservableMap sender, Object key) {
            notifyCallbacks(sender, 0, key);
        }
    }

    @Override
    public int size() {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public boolean containsKey(Object o) {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public boolean containsValue(Object o) {
        throw new IllegalStateException("Method not implemented");
    }


    @Override
    public Object put(String s, Object o) {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public Object remove(Object o) {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public void putAll(Map m) {
        throw new IllegalStateException("Method not implemented");
    }


    @Override
    public void clear() {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public Set<String> keySet() {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public Collection<Object> values() {
        throw new IllegalStateException("Method not implemented");
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new IllegalStateException("Method not implemented");
    }

}