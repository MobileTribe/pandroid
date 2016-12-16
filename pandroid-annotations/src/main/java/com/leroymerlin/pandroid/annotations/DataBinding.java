package com.leroymerlin.pandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by adrien on 29/09/2016.
 */

@Target(ElementType.TYPE)
public @interface DataBinding {
    boolean twoway() default true;
}
