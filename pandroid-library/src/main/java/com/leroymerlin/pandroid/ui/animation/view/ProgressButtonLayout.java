package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.ui.animation.AnimUtils;
import com.leroymerlin.pandroid.ui.animation.SimpleAnimatorListener;
import com.leroymerlin.pandroid.ui.loader.ProgressWheel;
import com.leroymerlin.pandroid.utils.DeviceUtils;

/**
 * Created by florian on 04/09/15.
 */
public class ProgressButtonLayout extends CircularFrameLayout {
    private View contentView;
    private ProgressWheel progressWheel;

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

    public View getButton() {
        return this.contentView;
    }

    public ProgressWheel getProgressWheel() {
        return progressWheel;
    }


    public void load(boolean anim) {
        if (contentView == null)
            return;
        int duration = anim ? getResources().getInteger(R.integer.anim_speed) : 1;
        progressWheel.setVisibility(VISIBLE);
        if (contentView.getMeasuredHeight() == 0) {
            AnimUtils.mesureView(contentView);
        }
        int buttonHeight = contentView.getMeasuredHeight() - contentView.getPaddingTop() - contentView.getPaddingBottom();
        progressWheel.setCircleRadius(buttonHeight / 2);
        progressWheel.animate().setDuration(duration).alpha(1);
        progressWheel.spin();
        animateToRadius(buttonHeight / 2, duration, new SimpleAnimatorListener());
    }

    public void stopLoading(boolean anim) {
        if (contentView == null)
            return;
        int duration = anim ? getResources().getInteger(R.integer.anim_speed) : 1;
        progressWheel.stopSpinning();
        progressWheel.animate().setDuration(duration).alpha(0);
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
            contentView = child;
            this.cachedCenterView = child;
            ((LayoutParams) params).gravity = Gravity.CENTER;
            super.addView(child, index, params);
            initChildView(getContext());

        }
    }

    public boolean isLoading() {
        return progressWheel.isSpinning();
    }
}
