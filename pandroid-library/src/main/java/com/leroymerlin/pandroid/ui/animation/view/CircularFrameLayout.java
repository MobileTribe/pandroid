package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.ui.animation.AnimUtils;

import androidx.annotation.IdRes;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by paillardf on 20/10/14.
 */
public class CircularFrameLayout extends FrameLayout {

    private static final String ARG_SUPER = "ARG_SUPER";
    private static final String ARG_RADIUS = "ARG_RADIUS";
    private static final String ARG_CENTER_X = "ARG_CENTER_X";
    private static final String ARG_CENTER_Y = "ARG_CENTER_Y";
    private static final String ARG_CENTER_VIEW_ID = "ARG_CENTER_VIEW_ID";
    private static final String ARG_CLIP_ENABLE = "ARG_CLIP_ENABLE";

    private final int mLayerType;
    Path mRevealPath;

    boolean mClipOutEnable;

    float mCenterX;
    float mCenterY;
    private float mRadius = -1;
    private boolean mMaxRadius;
    boolean centerCanMove;


    private final static boolean LOLLIPOP_PLUS = SDK_INT >= LOLLIPOP;
    private int animationDuration = -1;
    private float cachedMaxRadius = -1;

    private boolean viewAttached;
    protected View cachedCenterView;
    private int centerViewId;
    private boolean wasAttached;
    private Runnable firstAnimation;

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
            if (mRadius == -1) {
                mMaxRadius = true;
            }
            mCenterX = a.getDimension(R.styleable.CircularFrameLayout_centerX, mCenterX);
            mCenterY = a.getDimension(R.styleable.CircularFrameLayout_centerY, mCenterY);
            centerCanMove = a.getBoolean(R.styleable.CircularFrameLayout_centerMove, false);
            setCenterOnChild(a.getResourceId(R.styleable.CircularFrameLayout_centerOn, 0));
            setClipOutEnable(a.getBoolean(R.styleable.CircularFrameLayout_clipOut, mClipOutEnable));


            a.recycle();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle values = new Bundle();
        values.putParcelable(ARG_SUPER, super.onSaveInstanceState());
        values.putFloat(ARG_RADIUS, mRadius);
        values.putFloat(ARG_CENTER_X, mCenterX);
        values.putFloat(ARG_CENTER_Y, mCenterY);
        values.putInt(ARG_CENTER_VIEW_ID, centerViewId);
        values.putBoolean(ARG_CLIP_ENABLE, mClipOutEnable);
        return values;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle values = (Bundle) state;
        super.onRestoreInstanceState(values.getParcelable(ARG_SUPER));
        mRadius = values.getFloat(ARG_RADIUS);
        mCenterX = values.getFloat(ARG_CENTER_X);
        mCenterY = values.getFloat(ARG_CENTER_Y);
        setCenterOnChild(values.getInt(ARG_CENTER_VIEW_ID));
        setClipOutEnable(values.getBoolean(ARG_CLIP_ENABLE));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        viewAttached = true;
        wasAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewAttached = false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (firstAnimation != null) {
            firstAnimation.run();
            firstAnimation = null;
        }
        if (!mClipOutEnable || isOpen()) {
            return super.drawChild(canvas, child, drawingTime);
        }
        if (isClose()) {
            return false;
        }
        updateCenterView();
        final int state = canvas.save();
        mRevealPath.reset();
        mRevealPath.addCircle(mCenterX, mCenterY, getRevealRadius(), Path.Direction.CW);
        canvas.clipPath(mRevealPath);
        boolean isInvalidate = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(state);
        return isInvalidate;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cachedMaxRadius = -1;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        updateCenterView();
        float x = ev.getX() - mCenterX;
        float y = ev.getY() - mCenterY;
        if ((!mClipOutEnable && !isAnimated()) || Math.sqrt(x * x + y * y) <= getRevealRadius())
            return super.dispatchTouchEvent(ev);
        else
            return false;
    }

    @Override
    public void setBackground(Drawable background) {
        //Background is not supported in circularFrameLayout
    }

    @Override
    public void setBackgroundResource(int resid) {
        //Background is not supported in circularFrameLayout
    }

    public void setCenterOnChild(@IdRes Integer viewId) {
        if (viewId != null && viewId != 0) {
            centerViewId = viewId;
            updateCenterView();
        } else {
            centerViewId = 0;
            cachedCenterView = null;
        }
    }

    private void updateCenterView() {
        if (centerViewId == 0 && cachedCenterView == null)
            return;
        if (cachedCenterView == null) {
            cachedCenterView = findViewById(centerViewId);
        }
        if (cachedCenterView != null) {
            float[] center = AnimUtils.getCenterPositionRelativeTo(cachedCenterView, CircularFrameLayout.this);
            setCenter(center[0], center[1], true);
        }
    }

    /**
     * Epicenter of animation circle reveal
     *
     * @param centerPos
     */
    public void setCenter(float[] centerPos) {
        setCenter(centerPos[0], centerPos[1]);
    }

    /**
     * Epicenter of animation circle reveal
     *
     * @param centerX
     * @param centerY
     */
    public void setCenter(float centerX, float centerY) {
        setCenter(centerX, centerY, false);
    }

    private void setCenter(float centerX, float centerY, boolean followingView) {
        if (!followingView) {
            this.setCenterOnChild(null); // we remove followed view
        }

        if (mCenterX != centerX || mCenterY != centerY) {
            mCenterX = centerX;
            mCenterY = centerY;
            invalidate();
        }
    }


    public void setClipOutEnable(boolean clip) {
        if (mClipOutEnable != clip) {
            if (mLayerType != View.LAYER_TYPE_SOFTWARE) {
                setLayerType(clip ? View.LAYER_TYPE_SOFTWARE : mLayerType, null);
            }
            mClipOutEnable = clip;
            invalidate();
        }
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
        if (mRadius != radius) {
            mMaxRadius = radius == getMaxRadius();
            mRadius = radius;
            setVisibility(!isClose() ? VISIBLE : INVISIBLE);
            invalidate();
        }
    }

    /**
     * Circle radius size
     *
     * @hide
     */
    public float getRevealRadius() {
        if (mMaxRadius) {
            mRadius = getMaxRadius();
        }
        return mRadius;
    }


    public void open(final Animator.AnimatorListener listener) {
        animateToRadius(Float.MAX_VALUE, getAnimationDuration(), listener);
    }

    public void close(final Animator.AnimatorListener listener) {
        animateToRadius(0, getAnimationDuration(), listener);
    }

    public void animateToRadius(float radius, int duration, final Animator.AnimatorListener listener) {
        animate(Float.MIN_VALUE, radius, duration, listener);
    }


    private Animator circularReveal;

    public boolean isOpen() {
        return getMaxRadius() <= getRevealRadius();
    }

    public boolean isClose() {
        return 0 >= getRevealRadius();
    }

    public void animate(final float initFrom, final float initTo, final int duration, final Animator.AnimatorListener listener) {

        if (!viewAttached && !wasAttached) {
            firstAnimation = new Runnable() {
                @Override
                public void run() {
                    animate(initFrom, initTo, duration, listener);
                }
            };
        } else {
            //if animation was planed we cancel it
            firstAnimation = null;

            final float to = initTo == Float.MAX_VALUE ? getMaxRadius() : initTo;
            if (!viewAttached) {
                setRevealRadius(to);
                setClipOutEnable(!isOpen() && !isClose());
            } else {
                final float from = initFrom == Float.MIN_VALUE ? getRevealRadius() : initFrom;
                if (circularReveal != null && circularReveal.isRunning()) {
                    float currentRadius = getRevealRadius();
                    circularReveal.cancel();
                    circularReveal = null;
                    if (currentRadius == from && currentRadius != getRevealRadius()) { //
                        animate(getRevealRadius(), to, duration, listener);
                        return;
                    }
                }

                final boolean finalOpen = to >= getMaxRadius();

                circularReveal = getAnimation(from, to);
                circularReveal.setDuration(duration);
                final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
                circularReveal.setInterpolator(interpolator);
                circularReveal.addListener(new Animator.AnimatorListener() {

                    boolean cancelled;
                    double startTime;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        setVisibility(VISIBLE);
                        setClipOutEnable(!useCircularRevealAnimation());
                        startTime = System.currentTimeMillis();
                        if (listener != null)
                            listener.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (cancelled)
                            return;

                        if (to == 0) {
                            setClipOutEnable(false);
                        } else if (finalOpen) {
                            setClipOutEnable(false);
                        } else {
                            setClipOutEnable(true);
                        }
                        setRevealRadius(!finalOpen ? to : getMaxRadius());


                        if (listener != null)
                            listener.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        long endTime = System.currentTimeMillis();

                        cancelled = true;
                        if (listener != null) {
                            listener.onAnimationCancel(animation);
                        }
                        float value = interpolator.getInterpolation((float) (endTime - startTime) / animation.getDuration());

                        setRevealRadius(from + (to - from) * value);

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        if (listener != null)
                            listener.onAnimationRepeat(animation);
                    }
                });
                circularReveal.start();

            }
        }
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Animator getAnimation(float from, float to) {
        updateCenterView();
        if (useCircularRevealAnimation()) {
            return ViewAnimationUtils.createCircularReveal(this, (int) mCenterX, (int) mCenterY, from, to);
        } else {
            return ObjectAnimator.ofFloat(this, "revealRadius", from, to);
        }
    }

    private boolean useCircularRevealAnimation() {
        return LOLLIPOP_PLUS && (centerViewId == 0 || !centerCanMove);
    }


    public void setCenterCanMove(boolean canMove) {
        this.centerCanMove = canMove;
    }


    public float getMaxRadius() {
        if (cachedMaxRadius <= 0) {
            updateCenterView();
            int[] size = AnimUtils.getViewSize(this);
            int w = size[0];
            int h = size[1];
            double xSize = Math.max(mCenterX, w - mCenterX);
            double ySize = Math.max(mCenterY, h - mCenterY);
            double diago = Math.sqrt(xSize * xSize + ySize * ySize);
            cachedMaxRadius = (float) diago;
        }
        return cachedMaxRadius;
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


