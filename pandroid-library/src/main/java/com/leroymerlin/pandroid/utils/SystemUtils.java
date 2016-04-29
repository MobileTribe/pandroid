package com.leroymerlin.pandroid.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.List;

public class SystemUtils {

    /**
     * Version of the SDK on the current device
     *
     * @return
     */
    public static int getSystemVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * System type : for now only "Android", maybe distinguish phone and tablet later
     *
     * @return
     */
    public static String getSystemType() {
        return "Android";
    }

    /**
     * Locale of the device
     *
     * @param context
     * @return
     */
    public static String getSystemLocale(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static final String getApplicationPackage(Context context) {
        return context.getPackageName();
    }

    /**
     * Get the version code of the store (current application package)
     *
     * @param context
     * @return version Code of the application
     * @throws NameNotFoundException if application is not found : should not occurred because this is the running application itself
     */
    public static int getApplicationVersionCode(Context context) throws NameNotFoundException {
        PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pinfo.versionCode;
    }

    /**
     * Get the version name of the store (current application package)
     *
     * @param context
     * @return version Name of the application
     * @throws NameNotFoundException if application is not found : should not occurred because this is the running application itself
     */
    public static String getApplicationVersionName(Context context) throws NameNotFoundException {
        PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pinfo.versionName;
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }



    public static boolean isExternalStorageAvailable() {
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            // to know is we can neither read nor write
            mExternalStorageWriteable = false;
        }
        return mExternalStorageWriteable;
    }


    private static long getRamSizeAvailable(Context context) {
        long ramSize = 0;
        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memInfo = new MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        ramSize = memInfo.availMem / (1024 * 1024);
        return ramSize;
    }

    /**
     * Determine if the device is rooted.
     *
     * @return true if the device is rooted, false otherwise.
     */
    public static boolean isRootedDevice() {

        String[] rootCommands = {
                "su",
                "/system/bin/su",
                "/system/xbin/su",
                "which su",
                "/system/bin/which su",
                "/system/xbin/which su"
        };

        if (isAbleToRunCommand(rootCommands)) {
            return true;
        }

        String tags = android.os.Build.TAGS;
        if (tags != null && tags.contains("test-keys")) {
            return true;
        }

        if (superUserApkExists()) {
            return true;
        }

        return false;
    }

    private static boolean isAbleToRunCommand(String[] commands) {

        boolean executedSuccesfully = false;
        for (String command : commands) {
            try {
                Runtime.getRuntime().exec(command).destroy();
                executedSuccesfully = true;
            } catch (Exception e) {
            }
        }

        return executedSuccesfully;
    }

    private static boolean superUserApkExists() {

        try {
            if (new File("/system/app/Superuser.apk").exists()) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }


    public static void uninstallApplication(String packageName, Activity activity) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent deleteIntent = new Intent(Intent.ACTION_DELETE, uri);
        activity.startActivity(deleteIntent);
    }

    public static boolean isApplicationInstalled(String appPackage, Context context) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(appPackage)) return true;
        }
        return false;
    }

}