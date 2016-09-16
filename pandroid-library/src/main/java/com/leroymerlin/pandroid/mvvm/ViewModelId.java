package com.leroymerlin.pandroid.mvvm;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by adrien on 14/09/16.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewModelId {
    int value();
}
