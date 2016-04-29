package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.ui.animation.SimpleAnimatorListener;
import com.leroymerlin.pandroid.ui.loader.ProgressWheel;
import com.leroymerlin.pandroid.utils.DeviceUtils;

/**
 * Created by florian on 04/09/15.
 */
public class ProgressButtonLayout extends CircularFrameLayout {
    private Button button;
    private ProgressWheel progressWheel;
    private int initWidth;

    public ProgressButtonLayout(Context context) {
        super(context);
    }

    public ProgressButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initChildView(Context context) {
        progressWheel = new ProgressWheel(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        progressWheel.setVisibility(GONE);
        progressWheel.setAlpha(0);
        progressWheel.setBarWidth((int) DeviceUtils.dpToPx(context, 3));
        progressWheel.setBarColor(getResources().getColor(R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressWheel.setTranslationZ(DeviceUtils.dpToPx(context, 4));
        }
        super.addView(progressWheel, -1, params);
    }

    public Button getButton() {
        return this.button;
    }

    public ProgressWheel getProgressWheel() {
        return progressWheel;
    }


    public void load(boolean anim) {
        if (button == null)
            return;
        int duration = anim ? getResources().getInteger(R.integer.anim_speed) : 1;
        progressWheel.setVisibility(VISIBLE);
        progressWheel.setCircleRadius(button.getHeight() / 2);
        progressWheel.animate().setDuration(duration).alpha(1);
        progressWheel.spin();

        ValueAnimator textColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, getResources().getColor(R.color.transparent_white));
        textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ((TextView) button).setTextColor((Integer) animator.getAnimatedValue());
            }

        });
        textColorAnimator.setDuration(duration);
        textColorAnimator.start();


        initWidth = button.getWidth();

        //setCenter(getWidth() / 2, getHeight() / 2);

        animateToRadius(button.getHeight() / 2, duration, new SimpleAnimatorListener());
    }

    public void stopLoading(boolean anim) {
        if (button == null)
            return;
        int duration = anim ? getResources().getInteger(R.integer.anim_speed) : 1;

        progressWheel.stopSpinning();
        progressWheel.animate().setDuration(duration).alpha(0);
        ValueAnimator textColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.transparent_white), Color.WHITE);
        textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ((TextView) button).setTextColor((Integer) animator.getAnimatedValue());
            }

        });
        textColorAnimator.setDuration(duration);
        textColorAnimator.start();
        if (initWidth == 0)
            initWidth = getWidth();

        open(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressWheel.setVisibility(GONE);
            }
        });
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childCount = getChildCount();
        if (childCount > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        } else {
            button = (Button) child;
            ((LayoutParams) params).gravity = Gravity.CENTER;
            super.addView(child, index, params);
            initChildView(getContext());

        }
    }

    public boolean isLoading() {
        return progressWheel.isSpinning();
    }
}
