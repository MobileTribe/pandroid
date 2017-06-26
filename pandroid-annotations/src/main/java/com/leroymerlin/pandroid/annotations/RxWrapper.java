package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface RxWrapper {
    boolean stream() default false;

    boolean wrapResult() default false;
}

