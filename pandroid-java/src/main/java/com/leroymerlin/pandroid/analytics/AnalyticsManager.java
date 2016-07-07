package com.leroymerlin.pandroid.analytics;

import java.util.HashMap;

/**
 * Created by florian on 06/11/14.
 */
public interface AnalyticsManager {

    public void sendEvent(String categoryId, String action, String label, long value);

    public EventBuilder buildHitEvent(String categoryId, String action, String label, long value);

    public void sendScreen(String screen);

    public EventBuilder buildScreenEvent(String screen);

    public void sendTiming(String category, long duration, String variable, String label);

    public EventBuilder buildTimingEvent(String category, long duration, String variable, String label);

    public void addTracker(Tracker tracker);

    public void send(Event event);

    public TrackerBuilder newTrackerBuilder();

    public EventBuilder newEventBuilder();

    public interface Tracker {
        void create();
    }

    abstract class TrackerBuilder {

        protected String id;
        protected boolean autoTrack = false;
        protected Integer resources;
        protected String name;
        protected HashMap<String,String> parameters;

        public TrackerBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public TrackerBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public TrackerBuilder setResources(int resources) {
            this.resources = resources;
            return this;
        }

        public TrackerBuilder enableAutoTracking(boolean autoTrack) {
            this.autoTrack = autoTrack;
            return this;
        }

        public TrackerBuilder setParameters(HashMap<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public abstract Tracker build();
    }

    public interface Event {
        void send();
    }

    abstract class EventBuilder {

        protected String category;
        protected String label;
        protected String screen;
        protected Long duration;
        protected Long value;
        protected String trackerName;
        protected String variable;
        protected boolean newSession;
        protected String action;
        protected HashMap<Object,Object> params = new HashMap<>();


        public EventBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        public EventBuilder setLabel(String label) {
            this.label = label;
            return this;
        }

        public EventBuilder setAction(String action) {
            this.action = action;
            return this;
        }

        public EventBuilder setScreen(String screen) {
            this.screen = screen;
            return this;
        }

        public EventBuilder setValue(long value) {
            this.value = value;
            return this;
        }

        public EventBuilder setVariable(String variable) {
            this.variable = variable;
            return this;
        }


        public EventBuilder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public EventBuilder setTracker(String trackerName) {
            this.trackerName = trackerName;
            return this;
        }

        public EventBuilder setNewSession(boolean newSession) {
            this.newSession = newSession;
            return this;
        }

        public EventBuilder addParam(Object key, Object value) {
            this.params.put(key, value);
            return this;
        }


        public abstract Event build();


    }
}
