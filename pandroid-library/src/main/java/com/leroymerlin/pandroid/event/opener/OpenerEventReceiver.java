package com.leroymerlin.pandroid.event.opener;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.log.LogWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by florian on 12/11/14.
 */
public abstract class OpenerEventReceiver<V extends OpenerReceiverProvider, T extends Opener> implements EventBusManager.EventBusReceiver {

    private final static String TAG = "FragmentEventReceiver";

    protected List<String> filter = new ArrayList<String>();

    @Inject
    protected EventBusManager eventBusManager;
    @Inject
    protected LogWrapper logWrapper;

    protected WeakReference<V> refAttachedObject;


    @Override
    public List<String> getTags() {
        return null;
    }

    @Override
    public boolean handle(Object data) {
        if (data instanceof Opener && filter.contains(((Opener) data).getFilterTag())) {
            onOpenerReceived((T) data);
            return true;
        }
        return false;
    }

    protected abstract void onOpenerReceived(T opener);

    public void attach(V attachedObject) {
        PandroidApplication.get(attachedObject.provideActivity()).inject(this);
        this.refAttachedObject = new WeakReference<V>(attachedObject);
    }

    public void detach() {
        refAttachedObject = null;
    }

}
