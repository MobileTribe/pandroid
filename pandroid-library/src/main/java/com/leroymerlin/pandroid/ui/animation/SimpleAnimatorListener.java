package com.leroymerlin.pandroid.ui.animation;

import android.animation.Animator;

import androidx.annotation.Nullable;

/**
 * Created by florian on 21/10/2015.
 */
public class SimpleAnimatorListener implements Animator.AnimatorListener {


    protected Animator.AnimatorListener wrapListener;

    public SimpleAnimatorListener() {

    }

    public SimpleAnimatorListener(@Nullable Animator.AnimatorListener wrapListener) {
        this.wrapListener = wrapListener;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (wrapListener != null)
            wrapListener.onAnimationStart(animation);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (wrapListener != null)
            wrapListener.onAnimationEnd(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (wrapListener != null)
            wrapListener.onAnimationCancel(animation);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        if (wrapListener != null)
            wrapListener.onAnimationRepeat(animation);
    }
}
