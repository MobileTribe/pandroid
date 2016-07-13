package com.leroymerlin.pandroid.analytics;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by florian on 06/11/14.
 */
public interface AnalyticsManager {



    String EVENT_TYPE = "event_type";

    String EVENT_CATEGORY = "event_category";
    String EVENT_VARIABLE = "event_variable";
    String EVENT_ACTION = "event_action";
    String EVENT_LABEL = "event_label";
    String EVENT_VALUE = "event_value";
    String EVENT_DURATION = "event_duration";

    String USER_ID = "user_id";

    String NAVIGATION_PATH = "navigation_path";
    String HISTORY = "history";
    String NEW_SESSION = "new_session";
    String SCREEN_NAME = "screen_name";
    String SUBJECT_OBJECT = "subject_object";

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
