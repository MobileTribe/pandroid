package com.leroymerlin.pandroid;

// Generated code from Pandroid Plugin. Do not modify!

import com.leroymerlin.pandroid.app.PandroidConfig;
import com.leroymerlin.pandroid.app.PandroidMapper;
import com.leroymerlin.pandroid.demo.BuildConfig;
import com.leroymerlin.pandroid.demo.PandroidGeneratedClassWrapper;

import java.util.Arrays;
import java.util.List;

public class PandroidMapperImpl extends PandroidMapper {
    public static final List<String> LIBRARIES = Arrays.asList("butterknife", "icepick");

    public static final String PACKAGE = "com.leroymerlin.pandroid.demo";

    @Override
    public void setupConfig() {
        PandroidConfig.DEBUG = BuildConfig.DEBUG;
        PandroidConfig.APPLICATION_ID = BuildConfig.APPLICATION_ID;
        PandroidConfig.BUILD_TYPE = BuildConfig.BUILD_TYPE;
        PandroidConfig.FLAVOR = BuildConfig.FLAVOR;
        PandroidConfig.VERSION_CODE = BuildConfig.VERSION_CODE;
        PandroidConfig.VERSION_NAME = BuildConfig.VERSION_NAME;
        PandroidConfig.LIBRARIES = PandroidMapperImpl.LIBRARIES;
    }

    @Override
    public <T> List<T> getGeneratedInstances(Class<T> type, Object target) {
        return PandroidGeneratedClassWrapper.getGeneratedInstances(type, target);
    }
}
