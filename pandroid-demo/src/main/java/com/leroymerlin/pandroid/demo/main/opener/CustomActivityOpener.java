package com.leroymerlin.pandroid.demo.main.opener;

import android.app.Activity;

import com.leroymerlin.pandroid.app.PandroidActivity;
import com.leroymerlin.pandroid.event.opener.ActivityOpener;
import com.leroymerlin.pandroid.event.opener.Opener;

/**
 * Created by florian on 27/07/2017.
 */

public class CustomActivityOpener extends ActivityOpener {

    public String param;

    public CustomActivityOpener(String s) {
        super(OpenerActivity.class);
        this.param = s;
    }
}
