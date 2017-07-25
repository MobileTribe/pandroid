package com.leroymerlin.pandroid.event.opener;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by florian on 30/11/14.
 */
public abstract class Opener<T> implements Serializable {

    private static final long serialVersionUID = -5637422898022135590L;

    private static final String TAG = Opener.class.getName();
    public static final String ARG_OPENER = TAG + ".OPENER";
    private static final String OPEN_TAG = TAG + ".OPEN_TAG";
    private final Class<? extends T> classType;
    private String title;

    public Opener(Class<? extends T> classType) {
        this.classType = classType;
    }

    public Class<? extends T> getComponentClass() {
        return classType;
    }

    protected Bundle getArguments() {
        Bundle bundle = new Bundle();
        if (this instanceof Parcelable) {
            bundle.putParcelable(ARG_OPENER, (Parcelable) this);
        } else {
            bundle.putSerializable(ARG_OPENER, this);
        }
        return bundle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilterTag() {
        return OPEN_TAG + ":" + classType.getName();
    }

    @Override
    public String toString() {
        return getFilterTag();
    }


    static <T extends Opener> T getOpener(Bundle bundle) {
        if (bundle != null && bundle.containsKey(ARG_OPENER)) {
            return (T) bundle.get(ARG_OPENER);
        }
        return null;
    }
}
