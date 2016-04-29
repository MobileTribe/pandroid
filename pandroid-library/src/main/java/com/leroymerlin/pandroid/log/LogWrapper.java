package com.leroymerlin.pandroid.log;


public interface LogWrapper {

    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;

    void addLogger(LogWrapper logWrapper);

    void removeLogger(LogWrapper logWrapper);

    void setLogLevel(int level);

    void setDebug(boolean isDebug);

    void v(String tag, String msg);

    void v(String tag, String msg, Throwable tr);

    void d(String tag, String msg);

    void d(String tag, String msg, Throwable tr);

    void i(String tag, String msg);

    void i(String tag, String msg, Throwable tr);

    void w(String tag, String msg);

    void w(String tag, String msg, Throwable tr);

    void w(String tag, Throwable tr);

    void e(String tag, String msg);

    void e(String tag, Throwable tr);

    void e(String tag, String msg, Throwable tr);

    void wtf(String tag, String msg);

    void wtf(String tag, Throwable tr);

    void wtf(String tag, String msg, Throwable tr);

    void addKey(String key, String value);
}
