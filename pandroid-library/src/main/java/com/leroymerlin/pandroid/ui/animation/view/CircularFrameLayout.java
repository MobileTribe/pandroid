package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.ui.animation.AnimUtils;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by paillardf on 20/10/14.
 */
public class CircularFrameLayout extends FrameLayout {

    private final int mLayerType;
    Path mRevealPath;

    boolean mClipOutEnable;

    float mCenterX;
    float mCenterY;
    float mRadius;


    private final static boolean LOLLIPOP_PLUS = SDK_INT >= LOLLIPOP;
    private int animationDuration = -1;


    public CircularFrameLayout(Context context) {
        this(context, null);
    }

    public CircularFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRevealPath = new Path();
        mLayerType = getLayerType();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.CircularFrameLayout);

            mRadius = a.getDimension(R.styleable.CircularFrameLayout_revealRadius, mRadius);
            mCenterX = a.getDimension(R.styleable.CircularFrameLayout_centerX, mCenterX);
            mCenterY = a.getDimension(R.styleable.CircularFrameLayout_centerY, mCenterY);
            setClipOutEnable(a.getBoolean(R.styleable.CircularFrameLayout_clipOut, mClipOutEnable));

            a.recycle();
        }
    }

    /**
     * Epicenter of animation circle reveal
     *
     * @hide
     */
    public void setCenter(float centerX, float centerY) {
        mCenterX = centerX;
        mCenterY = centerY;
        invalidate();
    }


    public void setClipOutEnable(boolean clip) {
        if (clip != mClipOutEnable && mLayerType != View.LAYER_TYPE_SOFTWARE) {
            setLayerType(clip ? View.LAYER_TYPE_SOFTWARE : mLayerType, null);
        }
        mClipOutEnable = clip;
        invalidate();
    }

    public boolean isAnimated() {
        return circularReveal != null && circularReveal.isRunning();
    }

    public boolean isClipOutEnable() {
        return mClipOutEnable;
    }

    /**
     * Circle radius size
     *
     * @hide
     */
    public void setRevealRadius(float radius) {
        mRadius = radius;
        invalidate();
    }

    /**
     * Circle radius size
     *
     * @hide
     */
    public float getRevealRadius() {
        return mRadius;
    }


    public void open(final Animator.AnimatorListener listener) {
        float radius = getMaxRadius();
        animateToRadius(radius, getAnimationDuration(), listener);
    }

    public void close(final Animator.AnimatorListener listener) {
        animateToRadius(0, getAnimationDuration(), listener);
    }

    public void animateToRadius(float radius, int duration, final Animator.AnimatorListener listener) {
        animate(mRadius, radius, duration, listener);
    }


    private Animator circularReveal;

    public boolean isOpen() {
        return getMaxRadius() == getRevealRadius();
    }

    public boolean isClose() {
        return 0 == getRevealRadius();
    }

    public void animate(final float from, final float to, final int duration, final Animator.AnimatorListener listener) {
        if (circularReveal != null && circularReveal.isRunning())
            circularReveal.cancel();
        try {
            circularReveal = getAnimation(from, to);
            circularReveal.setDuration(duration);
            circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
            circularReveal.addListener(new Animator.AnimatorListener() {

                boolean cancelled;

                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                    setClipOutEnable(!LOLLIPOP_PLUS);
                    if (listener != null)
                        listener.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (cancelled)
                        return;
                    if (to == 0) {
                        setVisibility(INVISIBLE);
                        setClipOutEnable(false);
                    } else if (to >= getMaxRadius()) {
                        setClipOutEnable(false);

                    } else {
                        setClipOutEnable(true);
                    }

                    setRevealRadius(to);


                    if (listener != null)
                        listener.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    cancelled = true;
                    if (listener != null)
                        listener.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    if (listener != null)
                        listener.onAnimationRepeat(animation);
                }
            });
            circularReveal.start();
        } catch (IllegalStateException ignore) {
        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Animator getAnimation(float from, float to) {
        if (LOLLIPOP_PLUS) {
            return ViewAnimationUtils.createCircularReveal(this, (int) mCenterX, (int) mCenterY, from, to);
        } else {
            return ObjectAnimator.ofFloat(this, "revealRadius", from, to);
        }
    }

//    @Override
//    public void draw(Canvas canvas) {
//        if (!mClipOutEnable) {
//            super.draw(canvas);
//            return;
//        }
//
//        final int state = canvas.save();
//
//        mRevealPath.reset();
//        mRevealPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);
//
//        canvas.clipPath(mRevealPath);
//        super.draw(canvas);
//        canvas.restoreToCount(state);
//    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!mClipOutEnable)
            return super.drawChild(canvas, child, drawingTime);

        final int state = canvas.save();

        mRevealPath.reset();
        mRevealPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);

        canvas.clipPath(mRevealPath);
        boolean isInvalidate = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(state);
        return isInvalidate;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX() - mCenterX;
        float y = ev.getY() - mCenterY;
        if ((!mClipOutEnable && !isAnimated()) || Math.sqrt(x * x + y * y) <= mRadius)
            return super.dispatchTouchEvent(ev);
        else
            return false;
    }


    public float getMaxRadius() {
        int[] size = AnimUtils.getViewSize(this);
        int w = size[0];
        int h = size[1];
        double xSize = Math.max(mCenterX, w - mCenterX);
        double ySize = Math.max(mCenterY, h - mCenterY);
        double diago = Math.sqrt(xSize * xSize + ySize * ySize);
        return (float) diago;
    }


    public int getAnimationDuration() {
        if (animationDuration < 0) {
            return getResources().getInteger(R.integer.anim_speed);
        }
        return animationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }
}


