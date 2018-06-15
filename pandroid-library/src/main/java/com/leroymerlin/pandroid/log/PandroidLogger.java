package com.leroymerlin.pandroid.log;

import java.util.ArrayList;
import java.util.List;


public class PandroidLogger implements LogWrapper {

    private static PandroidLogger logger;

    public List<LogWrapper> loggers = new ArrayList<>();

    protected PandroidLogger() {
    }

    public static LogWrapper getInstance() {
        if (logger == null)
            logger = new PandroidLogger();
        return logger;
    }


    @Override
    public void addLogger(LogWrapper logWrapper) {
        loggers.add(logWrapper);
    }

    @Override
    public void removeLogger(LogWrapper logWrapper) {
        loggers.remove(logWrapper);
    }

    @Override
    public void setLogLevel(int level) {
        for (LogWrapper logger : loggers) {
            logger.setLogLevel(level);
        }
    }

    @Override
    public void setDebug(boolean isDebug) {
        for (LogWrapper logger : loggers) {
            logger.setDebug(isDebug);
        }
    }

    public void v(String tag, String msg) {
        for (LogWrapper logger : loggers) {
            logger.v(tag, msg);
        }
    }

    public void v(String tag, String msg, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.v(tag, msg, tr);
        }
    }

    public void d(String tag, String msg) {
        for (LogWrapper logger : loggers) {
            logger.d(tag, msg);
        }
    }

    public void d(String tag, String msg, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.d(tag, msg, tr);
        }
    }

    public void i(String tag, String msg) {
        for (LogWrapper logger : loggers) {
            logger.i(tag, msg);
        }
    }

    public void i(String tag, String msg, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.i(tag, msg, tr);
        }
    }

    public void w(String tag, String msg) {
        for (LogWrapper logger : loggers) {
            logger.w(tag, msg);
        }
    }

    public void w(String tag, String msg, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.w(tag, msg, tr);
        }
    }

    public void w(String tag, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.w(tag, tr);
        }
    }

    public void e(String tag, String msg) {
        for (LogWrapper logger : loggers) {
            logger.e(tag, msg);
        }
    }

    public void e(String tag, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.e(tag, tr);
        }
    }

    public void e(String tag, String msg, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.e(tag, msg, tr);
        }
    }

    public void wtf(String tag, String msg) {
        for (LogWrapper logger : loggers) {
            logger.wtf(tag, msg);
        }
    }

    public void wtf(String tag, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.wtf(tag, tr);
        }

    }

    public void wtf(String tag, String msg, Throwable tr) {
        for (LogWrapper logger : loggers) {
            logger.wtf(tag, msg, tr);
        }
    }

    @Override
    public void addKey(String key, String value) {
        for (LogWrapper logger : loggers) {
            logger.addKey(key, value);
        }
    }

}
