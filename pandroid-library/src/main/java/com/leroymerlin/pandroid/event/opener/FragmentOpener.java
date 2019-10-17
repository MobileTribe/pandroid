package com.leroymerlin.pandroid.event.opener;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by florian on 30/11/14.
 */
public class FragmentOpener extends Opener<Fragment> {

    private static final long serialVersionUID = -5637436898054135590L;

    public FragmentOpener(Class<? extends Fragment> classType) {
        super(classType);
    }

    public Fragment newInstance() throws IllegalAccessException, InstantiationException {
        Fragment fragment = getComponentClass().newInstance();
        fragment.setArguments(getArguments());
        return fragment;
    }

    @Nullable
    public static <T extends FragmentOpener> T getOpener(@NonNull Fragment fragment) {
        Bundle args = fragment.getArguments();
        return getOpener(args);
    }
}
