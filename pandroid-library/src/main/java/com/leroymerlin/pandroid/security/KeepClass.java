package com.leroymerlin.pandroid.security;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by florian on 13/02/15.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface KeepClass {
}
