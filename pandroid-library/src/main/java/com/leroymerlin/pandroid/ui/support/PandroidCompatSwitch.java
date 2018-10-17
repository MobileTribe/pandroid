package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.util.AttributeSet;

import com.leroymerlin.pandroid.R;

import androidx.appcompat.widget.SwitchCompat;


public class PandroidCompatSwitch extends SwitchCompat {

    public PandroidCompatSwitch(Context context) {
        this(context, null);
    }

    public PandroidCompatSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchStyle);
    }

    public PandroidCompatSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VectorCompatHelper.setupView(this, attrs);
    }


}
