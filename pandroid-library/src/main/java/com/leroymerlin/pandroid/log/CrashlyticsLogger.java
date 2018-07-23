package com.leroymerlin.pandroid.log;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.leroymerlin.pandroid.app.PandroidConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by florian on 04/02/16.
 */
public class CrashlyticsLogger extends SimpleLogger {

    private final Context context;
    private boolean initialized;

    public CrashlyticsLogger(Context context) {
        this.context = context;
        if (!PandroidConfig.DEBUG){
            initialize();
        }
    }

    private void initialize() {
        initialized = true;
        Fabric.with(context, new Crashlytics());

    }

    @Override
    public void setDebug(boolean isDebug) {
        super.setDebug(isDebug);
        if(!isDebug && !initialized)
            initialize();
    }

    @Override
    public boolean shouldLog(int i) {
        return !debuggable && i >= logLevel && initialized;
    }

    @Override
    public void debug(String tag, String msg, Throwable tr) {
        if(initialized) {
            Crashlytics.log(DEBUG, msg, tr != null ? tr.getMessage() : "");
        }
    }

    @Override
    public void verbose(String tag, String msg, Throwable tr) {
        if(initialized) {
            Crashlytics.log(VERBOSE, msg, tr != null ? tr.getMessage() : "");
        }
    }

    @Override
    public void info(String tag, String msg, Throwable tr) {
        if(initialized) {
            Crashlytics.log(INFO, msg, tr != null ? tr.getMessage() : "");
        }
    }

    @Override
    public void warn(String tag, String msg, Throwable tr) {
        if(initialized) {
            Crashlytics.log(WARN, msg, tr != null ? tr.getMessage() : "");
            Crashlytics.logException(tr);
        }
    }

    @Override
    public void error(String tag, String msg, Throwable tr) {
        if(initialized) {
            Crashlytics.log(ERROR, msg, tr != null ? tr.getMessage() : "");
            Crashlytics.logException(tr);
        }
    }

    @Override
    public void logAssert(String tag, String msg, Throwable tr) {
        if(initialized) {
            Crashlytics.log(ASSERT, msg, tr != null ? tr.getMessage() : "");
            Crashlytics.logException(tr);
        }
    }

    @Override
    public void addKey(String key, String value) {
        if(initialized) {
            Crashlytics.setString(key, value);
        }
    }

}
