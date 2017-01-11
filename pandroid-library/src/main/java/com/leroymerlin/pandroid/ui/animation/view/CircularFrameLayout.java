package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
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
    float mRadius;


    private final static boolean LOLLIPOP_PLUS = SDK_INT >= LOLLIPOP;
    private int animationDuration = -1;

    private boolean viewAttached;
    protected View cachedCenterView;
    private int centerViewId;

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
            setCenterOnChild(a.getResourceId(R.styleable.CircularFrameLayout_centerOn, -1));
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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewAttached = false;
    }

    @Override
    public void setBackground(Drawable background) {
        //Background is not supported in circularFrameLayout
    }


    public void setCenterOnChild(@IdRes int viewId) {
        if (viewId > 0) {
            centerViewId = viewId;
            updateCenterView();
        } else {
            centerViewId = -1;
            cachedCenterView = null;
        }
    }

    private void updateCenterView() {
        if (centerViewId <= 0 && cachedCenterView == null)
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
            this.setCenterOnChild(-1); // we remove followed view
        }

        if (mCenterX != centerX || mCenterY != centerY) {
            boolean wasOpen = isOpen();
            mCenterX = centerX;
            mCenterY = centerY;
            if (wasOpen) {
                setRevealRadius(getMaxRadius());
            }
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
            mRadius = radius;
            invalidate();
        }
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
        animate(Float.MIN_VALUE, radius, duration, listener);
    }


    private Animator circularReveal;

    public boolean isOpen() {
        return getMaxRadius() == getRevealRadius();
    }

    public boolean isClose() {
        return 0 == getRevealRadius();
    }

    public void animate(float from, final float to, final int duration, final Animator.AnimatorListener listener) {
        if (circularReveal != null && circularReveal.isRunning())
            circularReveal.cancel();


        final boolean finalOpen = to >= getMaxRadius();

        if (!viewAttached) {
            setRevealRadius(to);
            setClipOutEnable(!finalOpen);
        } else {
            if (from == Float.MIN_VALUE) {
                from = mRadius;
            }
            circularReveal = getAnimation(from, to);
            circularReveal.setDuration(duration);

            final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            final float finalFrom = from;
            circularReveal.setInterpolator(interpolator);
            circularReveal.addListener(new Animator.AnimatorListener() {

                boolean cancelled;
                double startTime;

                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                    setClipOutEnable(!LOLLIPOP_PLUS || centerViewId > 0);
                    startTime = System.currentTimeMillis();
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

                    setRevealRadius(finalFrom + (to - finalFrom) * value);

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

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Animator getAnimation(float from, float to) {
        if (LOLLIPOP_PLUS && centerViewId <= 0) {
            return ViewAnimationUtils.createCircularReveal(this, (int) mCenterX, (int) mCenterY, from, to);
        } else {
            return ObjectAnimator.ofFloat(this, "revealRadius", from, to);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mClipOutEnable) {
            super.draw(canvas);
            return;
        }

        final int state = canvas.save();

        mRevealPath.reset();
        mRevealPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);

        canvas.clipPath(mRevealPath);
        super.draw(canvas);
        canvas.restoreToCount(state);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!mClipOutEnable) {
            return super.drawChild(canvas, child, drawingTime);
        }

        updateCenterView();
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


