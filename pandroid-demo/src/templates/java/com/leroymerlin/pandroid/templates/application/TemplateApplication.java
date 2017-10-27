package com.leroymerlin.pandroid.templates.application;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.dagger.BaseComponent;
import com.leroymerlin.pandroid.dagger.PandroidModule;

/**
 * Created by florian on 20/09/2017.
 */

public class TemplateApplication extends PandroidApplication {

    @Override
    protected BaseComponent createBaseComponent() {
        return DaggerTemplateAppComponent.builder()
                .pandroidModule(new PandroidModule(this))
                .templateAppModule(new TemplateAppModule())
                .build();
    }
}
