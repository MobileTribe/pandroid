package com.leroymerlin.pandroid.persistence;

import com.leroymerlin.pandroid.annotations.RxWrapper;

/**
 * Created by adrienleroy on 05/01/15.
 */
public interface ConfigurationManager {

    interface Config {

        String getName();

        Object getDefaultValue();

        Class getType();

    }

    @RxWrapper(wrapResult = true)
    <T> T getConfig(Config field);

    void setConfig(Config field, Object object);

    void remove(Config config);
}
