package com.leroymerlin.pandroid.templates.application;

import com.leroymerlin.pandroid.dagger.PandroidComponent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by florian on 20/09/2017.
 */

@Singleton
@Component(modules = {TemplateAppModule.class})
public interface TemplateAppComponent extends PandroidComponent {



}
