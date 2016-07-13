package com.leroymerlin.pandroid.analytics.impl;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.leroymerlin.pandroid.analytics.AnalyticsManager;
import com.leroymerlin.pandroid.log.LogWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Implementation of AnalyticsManager for GoogleAnalytics
 * <p/>
 * Created by florian on 06/11/14.
 */
public class GoogleAnalyticsManager extends AnalyticsManager.AnalyticsProcessor implements AnalyticsManager {

    private static final String TAG = "GoogleAnalyticsManager";


    private GoogleAnalytics mAnalytics;
    private ArrayList<Tracker> mTrackers = new ArrayList<>();
    protected LogWrapper logWrapper;
    protected final Context context;

    @Inject
    public GoogleAnalyticsManager(Context context) {
        this.context = context;
    }

    public GoogleAnalytics getAnalytics() {
        if (mAnalytics == null) {
            mAnalytics = GoogleAnalytics.getInstance(context);
        }
        return mAnalytics;
    }

    public ArrayList<Tracker> getTrackers() {
        return mTrackers;
    }

    @Override
    public AnalyticsTracker track() {
        return new AnalyticsTracker(this);
    }

    @Override
    public void processParam(HashMap<String, Object> params) {

        for (Tracker tracker : mTrackers) {

            boolean session = params.containsKey(NEW_SESSION);
            if (params.containsKey(SCREEN_NAME)) {
                tracker.setScreenName(params.get(SCREEN_NAME).toString());
                // Send a screen view.
                HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
                screenViewBuilder = addMetrics(screenViewBuilder, params);
                screenViewBuilder = addDimensions(screenViewBuilder, params);
                if (session) {
                    screenViewBuilder.setNewSession();
                    session = false;
                }
                tracker.send(screenViewBuilder.build());
            }

            if (params.containsKey(EVENT_CATEGORY) && params.containsKey(EVENT_VARIABLE) && params.containsKey(EVENT_LABEL) && params.containsKey(EVENT_DURATION)) {
                HitBuilders.TimingBuilder timingBuilder = new HitBuilders.TimingBuilder()
                        .setCategory(String.valueOf(params.get(EVENT_CATEGORY)))
                        .setValue((Long) params.get(EVENT_VALUE))
                        .setVariable((String) params.get(EVENT_VARIABLE))
                        .setLabel(String.valueOf(params.get(EVENT_LABEL)));
                timingBuilder = addMetrics(timingBuilder, params);
                timingBuilder = addDimensions(timingBuilder, params);
                if (session) {
                    timingBuilder.setNewSession();
                    session = false;
                }
                tracker.send(timingBuilder.build());
            }

            if (params.containsKey(EVENT_CATEGORY) && params.containsKey(EVENT_VARIABLE) && params.containsKey(EVENT_LABEL) && params.containsKey(EVENT_ACTION)) {

                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                        .setCategory(String.valueOf(params.get(EVENT_CATEGORY)))
                        .setValue((Long) params.get(EVENT_VALUE))
                        .setAction((String) params.get(EVENT_ACTION))
                        .setLabel(String.valueOf(params.get(EVENT_LABEL)));
                eventBuilder = addMetrics(eventBuilder, params);
                eventBuilder = addDimensions(eventBuilder, params);
                if (session) {
                    eventBuilder.setNewSession();
                }
                tracker.send(eventBuilder.build());
            }

        }


    }

    private <T> T addDimensions(T builder, HashMap<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                int dimensionInteger = Integer.parseInt(entry.getKey());
                if (entry.getValue() instanceof String) {
                    if (builder instanceof HitBuilders.EventBuilder) {
                        ((HitBuilders.EventBuilder) builder).setCustomDimension(dimensionInteger, (String) entry.getValue());
                    } else if (builder instanceof HitBuilders.TimingBuilder) {
                        ((HitBuilders.TimingBuilder) builder).setCustomDimension(dimensionInteger, (String) entry.getValue());
                    } else if (builder instanceof HitBuilders.ScreenViewBuilder) {
                        ((HitBuilders.ScreenViewBuilder) builder).setCustomDimension(dimensionInteger, (String) entry.getValue());
                    }
                }
            } catch (NumberFormatException ignore) {
            }
        }
        return builder;
    }

    private <T> T addMetrics(T builder, HashMap<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                int metricInteger = Integer.parseInt(entry.getKey());
                if (entry.getValue() instanceof Float) {
                    if (builder instanceof HitBuilders.EventBuilder) {
                        ((HitBuilders.EventBuilder) builder).setCustomMetric(metricInteger, (Float) entry.getValue());
                    } else if (builder instanceof HitBuilders.TimingBuilder) {
                        ((HitBuilders.TimingBuilder) builder).setCustomMetric(metricInteger, (Float) entry.getValue());
                    } else if (builder instanceof HitBuilders.ScreenViewBuilder) {
                        ((HitBuilders.ScreenViewBuilder) builder).setCustomMetric(metricInteger, (Float) entry.getValue());
                    }
                }
            } catch (NumberFormatException ignore) {
            }

        }
        return builder;
    }

}
