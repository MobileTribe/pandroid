package com.leroymerlin.pandroid.dagger;

/**
 * Created by florian on 27/07/2017.
 */

public interface PandroidDaggerProvider {

    void inject(Object object);

    BaseComponent getBaseComponent();
}
