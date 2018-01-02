package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.leroymerlin.pandroid.R;


public class PandroidCompatEditText extends AppCompatEditText {

    public PandroidCompatEditText(Context context) {
        this(context, null);
    }

    public PandroidCompatEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public PandroidCompatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VectorCompatHelper.setupView(this, attrs);
    }


}
