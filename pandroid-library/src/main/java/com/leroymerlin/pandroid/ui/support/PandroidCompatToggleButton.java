package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.leroymerlin.pandroid.R;


public class PandroidCompatToggleButton extends ToggleButton {

    public PandroidCompatToggleButton(Context context) {
        this(context, null);
    }

    public PandroidCompatToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PandroidCompatToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VectorCompatHelper.setupView(this, attrs);
    }


}
