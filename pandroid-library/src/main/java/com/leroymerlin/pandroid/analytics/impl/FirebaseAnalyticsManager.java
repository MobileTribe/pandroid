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
        String eventName = "event";
        if (params.containsKey(Event.TYPE)) {
            eventName = String.valueOf(params.get(Event.TYPE));
        }
        for (Map.Entry<String, Object> set : params.entrySet()) {
            if (!set.getKey().equals(Event.TYPE)) {
                if (set.getKey().startsWith("user")) {
                    if (set.getKey().equals(Param.USER_ID)) {
                        mFirebaseAnalytics.setUserId(String.valueOf(set.getValue()));
                    } else {
                        mFirebaseAnalytics.setUserProperty(set.getKey(), String.valueOf(set.getValue()));
                    }
                } else {
                    bundle.putString(set.getKey(), String.valueOf(set.getValue()));
                }

                if (set.getKey().equals(Event.LABEL)) {
                    eventName += "_" + set.getValue();
                }
            }
        }


        mFirebaseAnalytics.logEvent(eventName, bundle);
    }

}
