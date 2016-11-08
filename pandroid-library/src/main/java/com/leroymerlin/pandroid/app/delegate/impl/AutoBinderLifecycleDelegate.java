package com.leroymerlin.pandroid.app.delegate.impl;

import android.support.annotation.NonNull;

import com.leroymerlin.pandroid.app.delegate.LifecycleDelegateAutoBinder;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.SimpleLifecycleDelegate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Mehdi on 07/11/2016.
 */

public class AutoBinderLifecycleDelegate extends SimpleLifecycleDelegate<Object> {

    private static final Map<Class<?>, Constructor<? extends LifecycleDelegateAutoBinder>> AUTO_BINDERS
            = new LinkedHashMap<>();


    @SuppressWarnings("unchecked")
    private static Constructor<? extends LifecycleDelegateAutoBinder> findBinder(Class<?> cls) {
        Constructor<? extends LifecycleDelegateAutoBinder> binderConstructor = AUTO_BINDERS.get(cls);
        if (binderConstructor != null) {
            return binderConstructor;
        }

        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            // ignore framework
            return null;
        }

        try {
            Class<? extends LifecycleDelegateAutoBinder> bindingClass = (Class<? extends
                    LifecycleDelegateAutoBinder>) Class.forName(clsName + "_LifecycleAutoBinder");
            binderConstructor = bindingClass.getConstructor(cls);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find LifecycleAutoBinder constructor for " +
                    clsName, e);
        }

        AUTO_BINDERS.put(cls, binderConstructor);
        return binderConstructor;
    }


    private static LifecycleDelegateAutoBinder createBinder(@NonNull Object target) {
        Class<?> targetClass = target.getClass();
        Constructor<? extends LifecycleDelegateAutoBinder> constructor =
                findBinder(targetClass);
        if (constructor == null) {
            return LifecycleDelegateAutoBinder.EMPTY;
        }

        try {
            return constructor.newInstance(target);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to create binding instance.", e);
        }
    }


    @Override
    public void onInit(Object target) {
        super.onInit(target);
        createBinder(target).bind();
    }
}
