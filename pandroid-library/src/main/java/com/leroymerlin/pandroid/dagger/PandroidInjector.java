package com.leroymerlin.pandroid.dagger;

/**
 * Created by florian on 27/07/2017.
 */

public interface PandroidInjector {

    void inject(Object object);

    <T extends BaseComponent> T getBaseComponent();
}
