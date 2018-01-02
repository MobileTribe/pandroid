package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.leroymerlin.pandroid.R;


public class PandroidCompatRadioButton extends AppCompatRadioButton {

    public PandroidCompatRadioButton(Context context) {
        this(context, null);
    }

    public PandroidCompatRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public PandroidCompatRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VectorCompatHelper.setupView(this, attrs);
    }


}
