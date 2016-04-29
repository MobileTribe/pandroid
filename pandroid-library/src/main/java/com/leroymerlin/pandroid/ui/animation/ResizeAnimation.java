package com.leroymerlin.pandroid.ui.animation;

/**
 * Created by paillardf on 19/02/2014.
 */

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * an animation for resizing the view.
 */
public class ResizeAnimation extends Animation {

    /**
     * The resize view
     */
    protected View mView;

    private float diffY;
    private float diffX;

    /**
     * The start height.
     */
    protected Integer fromHeight;
    /**
     * The start width.
     */
    protected Integer fromWidth;

    /**
     * The end height.
     */
    protected Integer toHeight;
    /**
     * The end width.
     */
    protected Integer toWidth;

    /**
     * the view inside the resize view that will not resize
     */
    protected View containView;


    /**
     * @param view the resize view
     */
    protected ResizeAnimation(View view) {
        this.mView = view;
    }

    public static final ResizeAnimation create(View view) {
        return new ResizeAnimation(view);
    }

    public ResizeAnimation setContainView(View containView) {
        this.containView = containView;
        return this;
    }

    public ResizeAnimation width(int toWidth) {
        this.toWidth = toWidth;
        return this;
    }


    public ResizeAnimation width(int fromWidth, int toWidth) {
        this.fromWidth = fromWidth;
        return width(toWidth);
    }

    public ResizeAnimation height(int toHeight) {
        this.toHeight = toHeight;
        return this;
    }


    public ResizeAnimation height(int fromHeight, int toHeight) {
        this.fromHeight = fromHeight;
        return height(toHeight);
    }

    public ResizeAnimation startAnimation() {
        mView.startAnimation(this);
        return this;
    }

    public ResizeAnimation startAnimation(AnimationListener listener) {
        this.setAnimationListener(listener);
        mView.startAnimation(this);
        return this;
    }

    public ResizeAnimation duration(long duration) {
        super.setDuration(duration);
        return this;
    }


    /* (non-Javadoc)
     * @see android.view.animation.Animation#applyTransformation(float, android.view.animation.Transformation)
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (diffX != 0)
            mView.getLayoutParams().width = fromWidth + (int) (diffX * interpolatedTime);
        if (diffY != 0)
            mView.getLayoutParams().height = fromHeight + (int) (diffY * interpolatedTime);
        mView.requestLayout();

    }

    /* (non-Javadoc)
     * @see android.view.animation.Animation#initialize(int, int, int, int)
     */
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

        if(fromWidth==null){
            fromWidth = width;
        }

        if(fromHeight==null){
            fromHeight = height;
        }


        if(toWidth==null){
            toWidth = width;
        }

        if(toHeight==null){
            toHeight = height;
        }


        diffX = toWidth - fromWidth;
        diffY = toHeight - fromHeight;

        if (containView != null) {
            if (diffX != 0)
                containView.getLayoutParams().width = (int) (fromWidth + diffX);
            if (diffY != 0)
                containView.getLayoutParams().height = (int) (fromHeight + diffY);
        }

    }

    /* (non-Javadoc)
     * @see android.view.animation.Animation#willChangeBounds()
     */
    @Override
    public boolean willChangeBounds() {
        return true;
    }
}