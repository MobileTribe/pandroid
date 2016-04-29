package com.leroymerlin.pandroid.event;

import android.app.FragmentManager;
import android.content.Context;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.log.LogWrapper;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by paillard.f on 05/03/2014.
 */
public abstract class AbstractReceiver<T> implements EventBusManager.EventBusReceiver {
    protected WeakReference<T> refAttachedObject;
    protected FragmentManager fragmentManager;

    @Inject
    protected EventBusManager eventBusManager;
    @Inject
    protected LogWrapper logWrapper;

    public void attach(Context context, T attachedObject, FragmentManager fragmentManager) {
        PandroidApplication.get(context).inject(this);
        this.refAttachedObject = new WeakReference<T>(attachedObject);
        this.fragmentManager = fragmentManager;
    }

    public void detach() {
        refAttachedObject = null;
        fragmentManager = null;
    }

    @Override
    public List<String> getTags() {
        return null;
    }
}
