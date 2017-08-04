package com.leroymerlin.pandroid.demo.main.opener;

import android.app.Fragment;

import com.leroymerlin.pandroid.event.opener.FragmentOpener;

/**
 * Created by florian on 27/07/2017.
 */

class ColorOpener extends FragmentOpener {
    final int color;

    public ColorOpener(int color) {
        super(ColorFragment.class);
        this.color = color;
    }
}
