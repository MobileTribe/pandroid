package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

//tag::RxWrapper[]
@Target(ElementType.METHOD)
public @interface RxWrapper {
    /**
     * if true the method will return an Observable, otherwise it will be a Single
     * @return false by default
     */
    boolean stream() default false;

    /**
     * if true the result will be wrap in a model (use to get multiple error or null response)
     * if the result is wrap no error will be throw to the stream
     * @return false by default
     */
    boolean wrapResult() default false;
}
//end::RxWrapper[]

