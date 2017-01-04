package com.leroymerlin.pandroid.event;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by florian on 30/11/14.
 */
public class FragmentOpener implements Serializable {

    private static final long serialVersionUID = -5637436898054135590L;

    private static final String TAG = FragmentOpener.class.getName();
    public static final String ARG_OPENER = TAG + ".OPENER";
    private static final String OPEN_TAG = TAG + ".OPEN_TAG";
    private final Class<? extends Fragment> classType;
    private String breadcrumbTitle;

    public FragmentOpener(Class<? extends Fragment> classType) {
        this.classType = classType;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return classType;
    }

    private Bundle getArguments() {
        Bundle b = new Bundle();
        if(this instanceof Parcelable){
            b.putParcelable(ARG_OPENER, (Parcelable) this);
        }else{
            b.putSerializable(ARG_OPENER, this);
        }
        return b;
    }

    public Fragment newInstance() throws IllegalAccessException, InstantiationException {
        Fragment fragment = getFragmentClass().newInstance();
        fragment.setArguments(getArguments());
        return fragment;
    }

    public String getBreadcrumbTitle() {
        return breadcrumbTitle;
    }

    public void setBreadcrumbTitle(String breadcrumbTitle) {
        this.breadcrumbTitle = breadcrumbTitle;
    }

    public String getFilterTag() {
        return OPEN_TAG + ":" + classType.getName();
    }

    @Override
    public String toString() {
        return getFilterTag();
    }
}
