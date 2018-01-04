package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.leroymerlin.pandroid.R;


public class PandroidCompatButton extends AppCompatButton {

    public PandroidCompatButton(Context context) {
        this(context, null);
    }

    public PandroidCompatButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public PandroidCompatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VectorCompatHelper.setupView(this, attrs);
    }


}
