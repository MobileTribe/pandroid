package com.leroymerlin.pandroid.app;

/**
 * Created by florian on 03/11/2016.
 */

public abstract class PandroidMapper {
    private static PandroidMapper instance;
    public static final String MAPPER_IMPL_NAME = "PandroidMapperImpl";
    public static final String MAPPER_PACKAGE = "com.leroymerlin.pandroid";


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


    //public abstract ReceiversProvider getReceiver(Object target);

}