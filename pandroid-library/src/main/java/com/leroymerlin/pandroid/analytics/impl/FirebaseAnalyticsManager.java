package com.leroymerlin.pandroid.analytics.impl;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.leroymerlin.pandroid.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by florian on 13/07/16.
 */
public class FirebaseAnalyticsManager extends AnalyticsManager.AnalyticsProcessor implements AnalyticsManager {

    protected final FirebaseAnalytics mFirebaseAnalytics;

    @Inject
    public FirebaseAnalyticsManager(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

    }

    @Override
    public AnalyticsTracker track() {
        return new AnalyticsTracker(this);
    }

    @Override
    public void processParam(HashMap<String, Object> params) {


        Bundle bundle = new Bundle();
        for (Map.Entry<String, Object> set : params.entrySet()) {
            if (!set.getKey().equals(EVENT_TYPE)) {
                if (set.getKey().startsWith("user")) {
                    if (set.getKey().equals(USER_ID)) {
                        mFirebaseAnalytics.setUserId(USER_ID);
                    } else {
                        mFirebaseAnalytics.setUserProperty(set.getKey(), String.valueOf(set.getValue()));
                    }
                } else {
                    bundle.putString(set.getKey(), String.valueOf(set.getValue()));
                }
            }
        }

        String eventName = "event";
        if (params.containsKey(EVENT_TYPE)) {
            eventName = String.valueOf(params.get(EVENT_TYPE));
        }
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }

}
