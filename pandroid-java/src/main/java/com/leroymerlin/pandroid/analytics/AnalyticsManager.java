package com.leroymerlin.pandroid.analytics;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by florian on 06/11/14.
 */
public interface AnalyticsManager {


    class Event {
        public static class Type {
            public static final String ACTION = "action";
            public static final String SCREEN = "screen";
            public static final String TIMER = "timer";
            protected Type(){}
        }

        public static final String TYPE = "type";
        public static final String CATEGORY = "category";
        public static final String VARIABLE = "variable";
        public static final String ACTION = "action";
        public static final String LABEL = "label";
        public static final String VALUE = "value";
        public static final String DURATION = "duration";
        protected Event() {
        }
    }

    class Param {
        public static final String USER_ID = "user_id";
        public static final String NAVIGATION_PATH = "navigation_path";
        public static final String HISTORY = "history";
        public static final String NEW_SESSION = "new_session";
        public static final String SUBJECT_OBJECT = "subject_object";
        protected Param(){}
    }

    AnalyticsTracker track();


    abstract class AnalyticsProcessor {

        public abstract void processParam(HashMap<String, Object> param);

    }

    class AnalyticsTracker {
        protected final AnalyticsProcessor processor;

        protected HashMap<String, Object> params = new LinkedHashMap<>();

        public AnalyticsTracker(AnalyticsProcessor processor) {
            this.processor = processor;

        }

        public AnalyticsTracker addParam(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public void send() {
            processor.processParam(params);
        }
    }

}
