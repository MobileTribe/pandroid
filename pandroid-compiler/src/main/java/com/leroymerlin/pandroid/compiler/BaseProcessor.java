package com.leroymerlin.pandroid.compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by paillardf on 16/12/2016.
 */

public abstract class BaseProcessor {

    protected final Elements mElementsUtils;
    protected final Types mTypesUtils;
    private boolean classGenerated;

    public BaseProcessor(Elements elements, Types types) {
        mElementsUtils = elements;
        mTypesUtils = types;
    }


    public abstract List<String> getSupportedAnnotations();

    public abstract void process(RoundEnvironment roundEnv, ProcessingEnvironment processingEnvironment);

    public abstract boolean useGeneratedAnnotation();

    protected String getClassName(Element element) {
        return element.getEnclosingElement().getSimpleName().toString();
    }

    protected String getMethodName(ExecutableElement element) {
        return element.getSimpleName().toString();
    }

    protected String getPackageName(Element element) throws Exception {
        PackageElement packageElement = mElementsUtils.getPackageOf(element);
        if (packageElement.isUnnamed()) {
            throw new Exception("Aucun package est indiqué pour " + element.getSimpleName());
        }
        return packageElement.getQualifiedName().toString();
    }

    protected String getFullName(ExecutableElement element) throws Exception {
        String packageName = getPackageName(element);
        String className = getClassName(element.getEnclosingElement());
        return packageName + "." + className + "." + getMethodName(element);
    }


    protected String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    protected void log(ProcessingEnvironment environment, String msg, Diagnostic.Kind level) {
        environment.getMessager().printMessage(level, msg);
    }

    public void setClassGenerated(boolean classGenerated) {
        this.classGenerated = classGenerated;
    }

    public boolean isClassGenerated(){
        return classGenerated;
    }

    protected void saveClass(ProcessingEnvironment environment, String packageName, TypeSpec.Builder classBuilder) {
        JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build())
                .build();
        javaFile.toJavaFileObject();
        try {
            javaFile.writeTo(environment.getFiler());
            classGenerated = true;
        } catch (IOException e) {
            log(environment, e.getMessage(), Diagnostic.Kind.ERROR);
        }
    }


}
