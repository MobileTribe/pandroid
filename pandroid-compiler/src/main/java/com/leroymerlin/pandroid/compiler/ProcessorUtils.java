package com.leroymerlin.pandroid.compiler;

/**
 * Created by florian on 21/06/2017.
 */

public class ProcessorUtils {

    private ProcessorUtils() {
    }


    public static String capitalize(String text) {
        if (text.isEmpty())
            return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
