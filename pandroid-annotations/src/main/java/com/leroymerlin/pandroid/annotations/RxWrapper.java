package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

//tag::RxWrapper[]
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RxWrapper {
    /**
     * if true the wrapper will return a single result and complete, otherwise it will return values until it is explicitly cancelled
     *
     * @return true by default
     */
    boolean singleValue() default true;

    /**
     * if true the result will be wrap in a model (use to get multiple error or null response)
     * if the result is wrap no error will be throw to the single
     *
     * @return false by default
     */
    boolean wrapResult() default false;
}
//end::RxWrapper[]

