package com.leroymerlin.pandroid.demo.main.opener;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;

/**
 * Created by florian on 27/07/2017.
 */

public class ColorFragment extends PandroidFragment<ColorOpener> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_color, container, false);
        inflate.setBackgroundColor(mOpener.color);
        return inflate;
    }
}
