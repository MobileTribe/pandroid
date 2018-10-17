package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.util.AttributeSet;

import com.leroymerlin.pandroid.R;

import androidx.appcompat.widget.AppCompatRadioButton;


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
