package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface BindLifeCycleDelegate {

    String BINDER_PREFIX = "_LifecycleAutoBinder";

}
