package com.leroymerlin.pandroid.ui.animation;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.ScrollView;

import org.jetbrains.annotations.NotNull;


/**
 * Created by florian on 20/10/2015.
 */
public class AnimUtils {


    /**
     * get center position of a view relative or not to a parent view
     *
     * @param view target view
     * @param root root view or null to get screen position
     * @return view center position relative to root parent or screen
     */
    public static float[] getCenterPositionRelativeTo(@NotNull View view, View root) {
        float[] result = getPositionRelativeTo(view, root);
        result[0] += view.getMeasuredWidth() / 2;
        result[1] += view.getMeasuredHeight() / 2;
        return result;
    }

    /**
     * get view position relative or not to a parent view
     *
     * @param view target view
     * @param root root view or null to get screen position
     * @return view position relative to root parent or screen
     */
    public static float[] getPositionRelativeTo(@NotNull View view, View root) {
        if (root == null) {
            int[] position = new int[2];
            view.getLocationOnScreen(position);
            return new float[]{position[0], position[1]};
        }
        float[] parentPos = null;
        if (view == root) {
            parentPos = new float[2];
        } else {
            parentPos = getPositionRelativeTo((View) view.getParent(), root);
            parentPos[0] += view.getX();
            parentPos[1] += view.getY();
        }

        if (view instanceof ScrollView) {
            parentPos[0] -= view.getScrollX();
            parentPos[1] -= view.getScrollY();
        }

        return parentPos;
    }

    /**
     * get or calculate view size
     *
     * @param view to measure
     * @return view calculated size
     */
    public static int[] getViewSize(@NotNull View view) {
        if (view.getHeight() == 0 && view.getWidth() == 0) {// try to calculate size
            if (view.getMeasuredWidth() == 0 && view.getMeasuredHeight() == 0) {
                if (view.getParent() != null && view.getParent() instanceof View) {
                    mesureView((View) view.getParent());
                } else {
                    mesureView(view);
                }
            }
            return new int[]{
                    view.getMeasuredWidth(), view.getMeasuredHeight()
            };
        }
        return new int[]{
                view.getWidth(), view.getHeight()
        };
    }

    /**
     * Measure a view with no contraint
     *
     * @param view to mesure
     */
    public static void mesureView(@NotNull View view) {
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static float getElevationRelativeTo(@NotNull View view, View root) {
        if (root == null) {
            return view.getElevation();
        }
        if (view == root) {
            return view.getElevation();
        } else {
            return view.getElevation() + getElevationRelativeTo((View) view.getParent(), root);
        }
    }
}
