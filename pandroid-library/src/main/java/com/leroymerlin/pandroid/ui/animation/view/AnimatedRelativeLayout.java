package com.leroymerlin.pandroid.ui.animation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by florian on 06/12/14.
 */
public class AnimatedRelativeLayout extends RelativeLayout {

    public AnimatedRelativeLayout(Context context) {
        super(context);
    }

    public AnimatedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private int getCalculedHeight() {
        int height = getHeight();
        if (height == 0 && getParent() != null && getParent() instanceof View) {
            height = ((View) getParent()).getHeight();
        }
        return height;
    }

    private int getCalculedWidth() {
        int width = getWidth();
        if (width == 0 && getParent() != null && getParent() instanceof View) {
            width = ((View) getParent()).getWidth();
        }
        return width;
    }


    public void setYFraction(final float fraction) {

        float translationY = getCalculedHeight() * fraction;
        setTranslationY(translationY);
    }

    public float getYFraction() {
        if (getCalculedHeight() == 0) {
            return 0;
        }
        return getTranslationY() / getCalculedHeight();
    }

    public void setXFraction(final float fraction) {
        float translationX = getCalculedWidth() * fraction;
        setTranslationX(translationX);
    }

    public float getXFraction() {
        if (getCalculedWidth() == 0) {
            return 0;
        }
        return getTranslationX() / getCalculedWidth();
    }
}