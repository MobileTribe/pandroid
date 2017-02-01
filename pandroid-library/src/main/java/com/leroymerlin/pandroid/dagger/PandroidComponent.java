package com.leroymerlin.pandroid.dagger;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mehdi on 30/11/2015.
 */
@Component(
        modules = PandroidModule.class
)
@Singleton
public interface PandroidComponent extends BaseComponent {



}
