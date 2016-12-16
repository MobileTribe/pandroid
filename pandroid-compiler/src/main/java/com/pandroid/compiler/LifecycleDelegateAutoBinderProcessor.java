package com.pandroid.compiler;

import com.leroymerlin.pandroid.app.delegate.LifecycleDelegateAutoBinder;
import com.leroymerlin.pandroid.annotations.BindLifeCycleDelegate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by Mehdi on 07/11/2016.
 */

public class LifecycleDelegateAutoBinderProcessor {

    private final Elements mElementsUtils;

    public LifecycleDelegateAutoBinderProcessor(Elements elementsUtils) {
        mElementsUtils = elementsUtils;
    }

    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith
                (BindLifeCycleDelegate.class);
        Map<ClassName, List<Element>> infosMap = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            ClassName parentClassName = ClassName.get(enclosingElement);

            List<Element> elements = infosMap.get(parentClassName);
            if (elements == null) {
                elements = new ArrayList<>();
                infosMap.put(parentClassName, elements);
            }
            elements.add(element);
        }

        for (ClassName className : infosMap.keySet()) {
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(
                            className.packageName(),
                            className.simpleName()
                    ), "target")
                    .addStatement("this.$L = new WeakReference<>($L)", "mTarget", "target");

            MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("final $T target = mTarget.get()", className)
                    .addStatement("final $T delegate = target.getPandroidDelegate()",
                            ClassName.get("com.leroymerlin.pandroid.app.delegate",
                                    "PandroidDelegate"))
                    .addAnnotation(Override.class)
                    .beginControlFlow("if (target != null && delegate != null)");


            for (Element element : infosMap.get(className)) {
                String illegalGenericConstructor = "throw new $T(\"empty constructor not exist\")";
                String elementClassName = element.asType().toString();
                TypeElement classElement = mElementsUtils.getTypeElement(elementClassName);
                for (Element enclosed : classElement.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                        ExecutableElement constructorElement = (ExecutableElement) enclosed;
                        if (constructorElement.getParameters().size() == 0 && constructorElement
                                .getModifiers().contains(Modifier.PUBLIC)) {
                            illegalGenericConstructor = null;
                            break;
                        }
                    }
                }

                bindBuilder.beginControlFlow("if (target.$L == null)", element);
                if (illegalGenericConstructor == null) {
                    bindBuilder.addStatement("target.$L = new $T()", element, classElement);
                } else {
                    bindBuilder.addStatement(illegalGenericConstructor, IllegalStateException.class);
                }
                bindBuilder.endControlFlow();

                bindBuilder.addStatement("delegate.addLifecycleDelegate(target" +
                        ".$L)", element);
            }
            bindBuilder.endControlFlow();

            TypeSpec.Builder modelBuilder = TypeSpec.classBuilder(className.simpleName() +
                    BindLifeCycleDelegate.BINDER_PREFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(LifecycleDelegateAutoBinder.class)
                    .addMethod(constructorBuilder.build())
                    .addField(ParameterizedTypeName.get(ClassName.get(WeakReference.class),
                            className), "mTarget", Modifier.PRIVATE, Modifier.FINAL)
                    .addMethod(bindBuilder.build());

            try {
                JavaFile javaFile = JavaFile.builder(className.packageName(), modelBuilder.build())
                        .build();
                javaFile.toJavaFileObject();
                javaFile.writeTo(processingEnvironment.getFiler());
            } catch (Exception e) {
                log(processingEnvironment, e.getMessage(), Diagnostic.Kind.ERROR);
            }
        }
    }

    private void log(ProcessingEnvironment environment, String msg, Diagnostic.Kind level) {
        environment.getMessager().printMessage(level, msg);
    }
}
