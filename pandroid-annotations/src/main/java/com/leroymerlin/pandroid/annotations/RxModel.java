package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
public @interface RxModel {
    /**
     * classes that match to implement the static method
     *
     * @return list of classes to implement the method
     */
    Class[] targets();
}
//end::RxWrapper[]

