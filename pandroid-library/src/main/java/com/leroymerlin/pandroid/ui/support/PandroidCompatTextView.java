package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class PandroidCompatTextView extends AppCompatTextView {

    public PandroidCompatTextView(Context context) {
        this(context, null);
    }

    public PandroidCompatTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public PandroidCompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VectorCompatHelper.setupView(this, attrs);
    }


}
