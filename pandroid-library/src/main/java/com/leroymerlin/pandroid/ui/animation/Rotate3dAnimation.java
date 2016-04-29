package com.leroymerlin.pandroid.ui.animation;


/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified angles.
 * This animation also adds a translation on the Z axis (depth) to improve the effect.
 */
public class Rotate3dAnimation extends Animation {

    public enum AXIS {
        X,
        Y,
        Z
    }

    public enum CENTER {
        TOP,
        TOP_LEFT,
        LEFT,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        BOTTOM,
        CENTER
    }


    private CENTER center;

    /**
     * The m from degrees.
     */
    private final float mFromDegrees;

    /**
     * The m to degrees.
     */
    private final float mToDegrees;

    /**
     * The m center x.
     */
    private float mCenterX;

    /**
     * The m center y.
     */
    private float mCenterY;

    /**
     * The m depth z.
     */
    private final float mDepthZ;

    /**
     * The m reverse.
     */
    private final boolean mReverse;

    /**
     * The m camera.
     */
    private Camera mCamera;

    private AXIS rotationAxis = AXIS.X;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees   the end angle of the 3D rotation
     * @param centerX     the X center of the 3D rotation
     * @param centerY     the Y center of the 3D rotation
     * @param depthZ      the depth z
     * @param reverse     true if the translation should be reversed, false otherwise
     */
    public Rotate3dAnimation(float fromDegrees, float toDegrees,
                             float centerX, float centerY, float depthZ, boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
    }


    public Rotate3dAnimation(float fromDegrees, float toDegrees,CENTER center, float depthZ, boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        this.center = center;
        mCenterX = 0;
        mCenterY = 0;
        mDepthZ = depthZ;
        mReverse = reverse;
    }


    /* (non-Javadoc)
     * @see android.view.animation.Animation#initialize(int, int, int, int)
     */
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
        if(center!=null){
            switch (center){

                case TOP:
                    mCenterX = width/2;

                    break;
                case TOP_LEFT:
                    break;
                case LEFT:
                    mCenterY = height/2;
                    break;
                case TOP_RIGHT:
                    mCenterX = width;
                    break;
                case RIGHT:
                    mCenterX = width;
                    mCenterY = height/2;
                    break;
                case BOTTOM_RIGHT:
                    mCenterX = width;
                    mCenterY = height;
                    break;
                case BOTTOM_LEFT:
                    mCenterY = height;
                    break;
                case BOTTOM:
                    mCenterX = width/2;
                    mCenterY = height;
                    break;
                case CENTER:
                    mCenterX = width/2;
                    mCenterY = height/2;
                    break;
            }
        }

    }

    public void setRotationAxis(AXIS rotationAxis) {
        this.rotationAxis= rotationAxis;
    }

    /* (non-Javadoc)
         * @see android.view.animation.Animation#applyTransformation(float, android.view.animation.Transformation)
         */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {


        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;


        final Matrix matrix = t.getMatrix();

        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        switch (rotationAxis) {
            case X:
                camera.rotateX(degrees);
                break;
            case Y:
                camera.rotateY(degrees);
                break;
            case Z:
                camera.rotateZ(degrees);
                break;
        }

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}