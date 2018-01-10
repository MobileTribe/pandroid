package com.leroymerlin.pandroid.ui.support;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.TextView;

import com.leroymerlin.pandroid.R;


class VectorCompatHelper {


    static void setupView(TextView view, AttributeSet attrs) {
        Context context = view.getContext();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PandroidCompat);
        try {
            int drawableLeftRes = typedArray.getResourceId(R.styleable
                    .PandroidCompat_drawableLeftPCompat, 0);
            int drawableTopRes = typedArray.getResourceId(R.styleable
                    .PandroidCompat_drawableTopPCompat, 0);
            int drawableRightRes = typedArray.getResourceId(R.styleable
                    .PandroidCompat_drawableRightPCompat, 0);
            int drawableBottomRes = typedArray.getResourceId(R.styleable
                    .PandroidCompat_drawableBottomPCompat, 0);

            Integer tintColor = null;
            if (typedArray.hasValue(R.styleable.PandroidCompat_drawableTintPCompat)) {
                tintColor = typedArray.getColor(R.styleable.PandroidCompat_drawableTintPCompat, 0);
            }
            Drawable drawableStart = getDrawableInternal(context, drawableLeftRes, tintColor);
            Drawable drawableTop = getDrawableInternal(context, drawableTopRes, tintColor);
            Drawable drawableEnd = getDrawableInternal(context, drawableRightRes, tintColor);
            Drawable drawableBottom = getDrawableInternal(context, drawableBottomRes, tintColor);

            view.setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
        } finally {
            typedArray.recycle();
        }
    }

    private static Drawable getDrawableInternal(Context context, @DrawableRes int drawableRes, Integer tintColor) {
        if (drawableRes == 0) {
            return null;
        }
        Drawable drawable = AppCompatResources.getDrawable(context, drawableRes);
        if (drawable != null && tintColor != null) {
            Drawable tintDrawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(tintDrawable, tintColor);
            return tintDrawable;
        }
        return drawable;
    }
}
