package com.leroymerlin.pandroid.app;

import java.util.List;

/**
 * Created by florian on 03/11/2016.
 */

public abstract class PandroidMapper {
    private static PandroidMapper instance;
    public static final String MAPPER_IMPL_NAME = "PandroidMapperImpl";
    public static final String WRAPPER_NAME = "PandroidGeneratedClassWrapper";
    public static final String WRAPPER_GENERATED_METHOD_NAME = "getGeneratedInstances";
    public static final String WRAPPER_INJECT_METHOD_NAME = "injectToTarget";
    public static final String WRAPPER_BASE_COMPONENT = "com.leroymerlin.pandroid.dagger.BaseComponent";
    public static final String MAPPER_PACKAGE = "com.leroymerlin.pandroid";
    public static final String PACKAGE_ATTR = "PACKAGE";


    public static PandroidMapper getInstance() {
        if (instance == null) {
            try {
                String configMapper = PandroidMapper.MAPPER_PACKAGE + "." + PandroidMapper.MAPPER_IMPL_NAME;
                Class<?> mapperClass = Class.forName(configMapper);
                instance = (PandroidMapper) mapperClass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(MAPPER_IMPL_NAME + " has not been generated. Please check the Pandroid Plugin Configuration", e);
            }
        }
        return instance;
    }

    public abstract void setupConfig();



    public abstract <T> List<T> getGeneratedInstances(Class<T> type, Object target);
    public abstract void injectToTarget(Object component, Object target);

}
