package com.leroymerlin.pandroid.log;

public class LogcatLogger extends SimpleLogger {

    private static LogcatLogger logger;
    protected LogcatLogger() {
    }

    public static LogWrapper getInstance() {
        if(logger==null)
            logger = new LogcatLogger();
        return logger;
    }

    private static String getLineTag(String tag) {
        int i = 4;
        String fullClassName = Thread.currentThread().getStackTrace()[i].getClassName();

        while (fullClassName.contains(SimpleLogger.class.getPackage().getName())){
            i++;
            fullClassName = Thread.currentThread().getStackTrace()[i].getClassName();
        }
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[i].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[i].getLineNumber();
        return tag + " (" + className + ".java:" + lineNumber + ")  " + methodName;
    }


    @Override
    void debug(String tag, String msg, Throwable tr) {
        android.util.Log.d(getLineTag(tag), msg, tr);
    }

    @Override
    void verbose(String tag, String msg, Throwable tr) {
        android.util.Log.v(getLineTag(tag), msg, tr);
    }

    @Override
    void info(String tag, String msg, Throwable tr) {
        android.util.Log.i(getLineTag(tag), msg, tr);
    }

    @Override
    void warn(String tag, String msg, Throwable tr) {
        android.util.Log.w(getLineTag(tag), msg, tr);
    }

    @Override
    void error(String tag, String msg, Throwable tr) {
        android.util.Log.e(getLineTag(tag), msg, tr);
    }

    @Override
    void logAssert(String tag, String msg, Throwable tr) {
        android.util.Log.wtf(getLineTag(tag), msg, tr);
    }

}
