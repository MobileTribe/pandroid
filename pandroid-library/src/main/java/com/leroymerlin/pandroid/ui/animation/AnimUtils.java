package com.leroymerlin.pandroid.ui.animation;

import android.view.View;
import android.widget.ScrollView;

/**
 * Created by florian on 20/10/2015.
 */
public class AnimUtils {


    public static float[] getPositionRelativeTo(View view, View root) {
        if (root == null) {
            int[] position = new int[2];
            view.getLocationOnScreen(position);
            return new float[]{position[0], position[1]};
        }
        float[] parentPos = null;
        if(view == root){
            parentPos = new float[2];
        }else{
            parentPos =  getPositionRelativeTo((View) view.getParent(), root);
        }
        parentPos[0] += view.getX();
        parentPos[1] += view.getY();

        if(view instanceof ScrollView){
            parentPos[0] -= view.getScrollX();
            parentPos[1] -= view.getScrollY();
        }
        return parentPos;
    }

    public static int[] getViewSize(View view) {
        if (view.getHeight() == 0 && view.getWidth() == 0) {// try to calculate size
            if (view.getParent() != null && view.getParent() instanceof View) {
                mesureView((View) view.getParent());
            } else {
                mesureView(view);
            }
            return new int[]{
                    view.getMeasuredWidth(), view.getMeasuredHeight()
            };
        }
        return new int[]{
                view.getWidth(), view.getHeight()
        };
    }

    public static void mesureView(View view) {
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }
}
