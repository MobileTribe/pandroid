package com.leroymerlin.pandroid.persistence;

/**
 * Created by adrienleroy on 05/01/15.
 */
public interface ConfigurationManager {

    public interface Config {

        public String getName();

        public Object getDefaultValue();

        public Class getType();

    }

    public <T> T getConfig(Config field);

    public void setConfig(Config field, Object object);

    public void remove(Config config);
}
