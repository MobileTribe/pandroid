package com.leroymerlin.pandroid.analytics.impl;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.leroymerlin.pandroid.analytics.AnalyticsManager;
import com.leroymerlin.pandroid.log.LogWrapper;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by florian on 06/11/14.
 */
public class GoogleAnalyticsManager implements AnalyticsManager {

    private static final String TAG = "GoogleAnalyticsManager";

    private GoogleAnalytics mAnalytics;
    HashMap<String, com.google.android.gms.analytics.Tracker> mTrackers = new HashMap<>();

    LogWrapper logWrapper;

    final Context context;

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

    public HashMap<String, com.google.android.gms.analytics.Tracker> getTrackers() {
        return mTrackers;
    }

    @Override
    public void sendEvent(String categoryId, String action, String label, long value) {
        send(buildHitEvent(categoryId, action, label, value).build());
    }

    @Override
    public EventBuilder buildHitEvent(String categoryId, String action, String label, long value) {
        return newEventBuilder().setCategory(categoryId).setAction(action).setLabel(label).setValue(value);
    }

    @Override
    public void sendScreen(String screen) {
        send(buildScreenEvent(screen).build());
    }

    @Override
    public EventBuilder buildScreenEvent(String screen) {
        return newEventBuilder().setScreen(screen);
    }

    @Override
    public void sendTiming(String category, long duration, String variable, String label) {
        send(buildTimingEvent(category, duration, variable, label).build());
    }

    @Override
    public EventBuilder buildTimingEvent(String category, long duration, String variable, String label) {
        return newEventBuilder().setCategory(category).setDuration(duration).setVariable(variable).setLabel(label);
    }

    @Override
    public void addTracker(Tracker tracker) {
        tracker.create();
    }

    @Override
    public void send(Event event) {
        event.send();
    }


    /**
     * init a new tracker builder
     *
     * @return new instance of trackerBuilder
     */
    @Override
    public TrackerBuilder newTrackerBuilder() {
        return new AnalyticsTrackerBuilder();
    }

    /**
     * init a new event builder
     *
     * @return
     */
    @Override
    public EventBuilder newEventBuilder() {
        return new AnalyticsEventBuilder();
    }

    protected class AnalyticsTrackerBuilder extends TrackerBuilder {

        @Override
        public Tracker build() {

            return new Tracker() {
                @Override
                public void create() {
                    if (!mTrackers.containsKey(name)) {
                        com.google.android.gms.analytics.Tracker t = null;
                        if (resources != null) {
                            t = getAnalytics().newTracker(resources);
                        } else {
                            t = getAnalytics().newTracker(id);
                        }
                        t.enableAutoActivityTracking(autoTrack);

                        if (parameters != null) {
                            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                                t.set(entry.getKey(), entry.getValue());
                            }
                        }

                        mTrackers.put(name, t);


                    } else {
                        logWrapper.w(TAG, "tracker named '" + name + "' already exist");
                    }
                }
            };

        }
    }

    protected class AnalyticsEventBuilder extends EventBuilder {


        @Override
        public Event build() {
            return new Event() {
                @Override
                public void send() {
                    for (Map.Entry<String, com.google.android.gms.analytics.Tracker> entry : mTrackers.entrySet()) {
                        if (trackerName == null || entry.getKey().equals(trackerName)) {
                            com.google.android.gms.analytics.Tracker tracker = entry.getValue();

                            boolean session = newSession;
                            if (screen != null) {
                                tracker.setScreenName(screen);
                                // Send a screen view.
                                HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
                                addMetrics(screenViewBuilder);
                                addDimensions(screenViewBuilder);
                                if (session) {
                                    screenViewBuilder.setNewSession();
                                    session = false;
                                }
                                tracker.send(screenViewBuilder.build());
                            }

                            if (category != null && variable != null && label != null && duration != null) {
                                HitBuilders.TimingBuilder timingBuilder = new HitBuilders.TimingBuilder()
                                        .setCategory(category)
                                        .setValue(duration)
                                        .setVariable(variable)
                                        .setLabel(label);
                                addMetrics(timingBuilder);
                                addDimensions(timingBuilder);
                                if (session) {
                                    timingBuilder.setNewSession();
                                    session = false;
                                }
                                tracker.send(timingBuilder.build());
                            }

                            if (category != null && action != null && label != null && value != null) {

                                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                                        .setCategory(category)
                                        .setAction(action)
                                        .setLabel(label)
                                        .setValue(value);
                                addMetrics(eventBuilder);
                                addDimensions(eventBuilder);
                                if (session) {
                                    eventBuilder.setNewSession();
                                }
                                tracker.send(eventBuilder.build());
                            }

                        }
                    }
                }

                private HitBuilders.EventBuilder addDimensions(HitBuilders.EventBuilder builder) {
                    if (dimensions != null) {
                        for (Map.Entry<Integer, String> entry : dimensions.entrySet()) {
                            builder.setCustomDimension(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder;
                }

                private HitBuilders.EventBuilder addMetrics(HitBuilders.EventBuilder builder) {
                    if (metrics != null) {
                        for (Map.Entry<Integer, Float> entry : metrics.entrySet()) {
                            builder.setCustomMetric(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder;
                }

                private HitBuilders.TimingBuilder addMetrics(HitBuilders.TimingBuilder builder) {
                    if (metrics != null) {
                        for (Map.Entry<Integer, Float> entry : metrics.entrySet()) {
                            builder.setCustomMetric(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder;
                }

                private HitBuilders.TimingBuilder addDimensions(HitBuilders.TimingBuilder builder) {
                    if (dimensions != null) {
                        for (Map.Entry<Integer, String> entry : dimensions.entrySet()) {
                            builder.setCustomDimension(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder;
                }

                private HitBuilders.ScreenViewBuilder addMetrics(HitBuilders.ScreenViewBuilder builder) {
                    if (metrics != null) {
                        for (Map.Entry<Integer, Float> entry : metrics.entrySet()) {
                            builder.setCustomMetric(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder;
                }

                private HitBuilders.ScreenViewBuilder addDimensions(HitBuilders.ScreenViewBuilder builder) {
                    if (dimensions != null) {
                        for (Map.Entry<Integer, String> entry : dimensions.entrySet()) {
                            builder.setCustomDimension(entry.getKey(), entry.getValue());
                        }
                    }
                    return builder;
                }
            };

        }


    }
}
