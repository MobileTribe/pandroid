package com.leroymerlin.pandroid.utils.config;

import com.leroymerlin.pandroid.persistence.ConfigurationManager;

/**
 * Created by florian on 12/01/15.
 */
public class SharedConfig implements ConfigurationManager.Config {
    private final String name;
    private final Object defaultValue;
    private final Class type;

    public SharedConfig(String name, Object defaultValue, Class type) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class getType() {
        return type;
    }

}
