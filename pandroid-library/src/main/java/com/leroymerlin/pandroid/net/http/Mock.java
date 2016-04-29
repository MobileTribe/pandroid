package com.leroymerlin.pandroid.net.http;

/**
 * Created by florian on 16/02/16.
 */

import com.leroymerlin.pandroid.net.mock.ServiceMock;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enables the mocking for the network regarding to mock type
 *
 * @author Orhan Obut
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Mock {
    int statusCode() default 200;

    int delay() default 200;

    String path() default "";

    boolean enable() default true;

    Class<? extends ServiceMock> mockClass() default ServiceMock.class;
}