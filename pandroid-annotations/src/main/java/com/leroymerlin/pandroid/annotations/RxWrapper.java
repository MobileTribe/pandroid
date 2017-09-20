package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

//tag::RxWrapper[]
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RxWrapper {
    /**
     * if true the method will return an Single, otherwise it will be a Observable
     *
     * @return true by default
     */
    boolean single() default true;

    /**
     * if true the result will be wrap in a model (use to get multiple error or null response)
     * if the result is wrap no error will be throw to the single
     *
     * @return false by default
     */
    boolean wrapResult() default false;
}
//end::RxWrapper[]

