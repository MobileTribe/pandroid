package com.leroymerlin.pandroid.compiler;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

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


    public static boolean sameMethods(Types typeUtils,
                                                  ExecutableElement method1,
                                                  ExecutableElement method2) {
        if (!method1.getSimpleName().equals(method2.getSimpleName()))
            return false;
        List<? extends VariableElement> parameters1 = method1
                .getParameters();
        List<? extends VariableElement> parameters2 = method2
                .getParameters();
        if (parameters1.size() != parameters2.size())
            return false;
        for (int i = 0; i < parameters1.size(); i++) {
            if (!typeUtils.isSameType(parameters1.get(i).asType(),
                    parameters2.get(i).asType()))
                return false;
        }
        return true;
    }
}
