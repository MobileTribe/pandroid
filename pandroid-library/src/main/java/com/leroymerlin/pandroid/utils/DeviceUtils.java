package com.leroymerlin.pandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * * This library provide methods to have informations about the device
 */
public class DeviceUtils {

    public static int getAPILevel() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getModel() {
        return android.os.Build.MODEL;
    }

    public static String getCarrier() {
        return android.os.Build.BRAND;
    }

    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }


    public static float spToPx(Context context, float sp) {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()));
    }

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * Check if a pacakge exist on the system
     *
     * @param targetPackage
     * @param context
     * @return
     */
    public static boolean doesPackageExist(String targetPackage, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void closeKeyboard(Activity activity) {
        if(activity == null)
            return;
        View view = activity.getCurrentFocus();
        if (view != null) {
            closeKeyboard(view);
        }
    }

    public static void closeKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void openKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }


}
