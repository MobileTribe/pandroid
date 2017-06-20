package com.leroymerlin.pandroid.compiler;

import com.google.common.collect.Lists;
import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.future.ActionDelegate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * Created by paillardf on 20/06/2017.
 */

public class RxWrapperProcessor extends BaseProcessor {


    static final ClassName SINGLE_TYPE = ClassName.get("io.reactivex", "Single");
    static final ClassName OBSERVABLE_TYPE = ClassName.get("io.reactivex", "Observable");
    static final ClassName RXACTIONDELEGATE_TYPE = ClassName.get("com.leroymerlin.pandroid.future", "RxActionDelegate");
    static final ClassName RXACTIONDELEGATE_TYPE = ClassName.get("com.leroymerlin.pandroid.future", "ActionDelegate");
    static final ClassName RXACTIONDELEGATE_SUBSCRIBEACTION_TYPE = RXACTIONDELEGATE_TYPE.nestedClass("OnSubscribeAction");
    static final ClassName RXACTIONDELEGATE_RESULT_TYPE = RXACTIONDELEGATE_TYPE.nestedClass("Result");


    public RxWrapperProcessor(Elements elements, Types types) {
        super(elements, types);
    }

    @Override
    public List<String> getSupportedAnnotations() {
        return Lists.newArrayList(RxWrapper.class.getCanonicalName());
    }

    @Override
    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
        if (!checkRxEnabled(processingEnvironment)) return;

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith
                (RxWrapper.class);
        List<TypeElement> classesToProcess = new ArrayList<>();

        for (Element element : elementsAnnotatedWith) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            if (!classesToProcess.contains(enclosingElement)) {
                classesToProcess.add(enclosingElement);
            }
        }

        TypeMirror actionDelegateTypeMirror = mElementsUtils.getTypeElement(ActionDelegate.class.getCanonicalName()).asType();


        for (TypeElement classType : classesToProcess) {
            ClassName className = ClassName.get(classType);
            if (classType.getKind().isInterface()) {
                String mTargetVar = "mTarget";
                TypeSpec.Builder modelBuilder = TypeSpec.classBuilder("Rx" + Character.toUpperCase(className.simpleName().charAt(0)) + className.simpleName().substring(1))
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(className)
                        .addField(className, mTargetVar, Modifier.PRIVATE, Modifier.FINAL);
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(
                                className.packageName(),
                                className.simpleName()
                        ), "target")
                        .addStatement("this.$L = $L", mTargetVar, "target");

                modelBuilder.addMethod(constructorBuilder.build());

                for (Element element : ElementFilter.methodsIn(classType.getEnclosedElements())) {
                    ExecutableElement method = (ExecutableElement) element;

                    RxWrapper rxWrapperAnnotation = method.getAnnotation(RxWrapper.class);
                    if (rxWrapperAnnotation != null) {

                        boolean single = rxWrapperAnnotation.single();


                        List<? extends VariableElement> parameters = method.getParameters();
                        List<ParameterSpec> newParameters = new ArrayList<>();
                        TypeName actionDelegateType = null;
                        int delegateIndex = 0;
                        String wrapArgStr = "";
                        for (int i = parameters.size() - 1; i >= 0; i--) {
                            VariableElement variableElement = parameters.get(i);
                            if (actionDelegateType == null && mTypesUtils.isAssignable(mTypesUtils.erasure(variableElement.asType()), actionDelegateTypeMirror)) {
                                actionDelegateType = ((ParameterizedTypeName) ParameterizedTypeName.get(parameters.get(i).asType())).typeArguments.get(0);
                                delegateIndex = i;
                            } else {
                                TypeName typeName = TypeName.get(variableElement.asType());
                                ParameterSpec.Builder builder = ParameterSpec.builder(typeName, variableElement.getSimpleName().toString());
                                ArrayList<Modifier> modifiers = new ArrayList<>(variableElement.getModifiers());
                                if (!modifiers.contains(Modifier.FINAL)) {
                                    modifiers.add(Modifier.FINAL);
                                }
                                builder.addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));
                                newParameters.add(builder.build());
                            }
                            wrapArgStr = variableElement.getSimpleName().toString() + (wrapArgStr.equals("") ? "" : ", ") + wrapArgStr;

                        }


                        if (method.getReturnType().equals(TypeName.VOID)) {
                            throw new IllegalStateException("Method should have void as return to be RxWrapped. " + className.toString() + ":" + method.getSimpleName().toString());
                        } else if (actionDelegateType == null) {
                            throw new IllegalStateException("Method should have an ActionDelegate type in the parameters to be RxWrapped. " + className.toString() + ":" + method.getSimpleName().toString());
                        }


                        ParameterizedTypeName singleType = ParameterizedTypeName.get(SINGLE_TYPE, actionDelegateType);
                        ParameterizedTypeName observableType = ParameterizedTypeName.get(OBSERVABLE_TYPE, ParameterizedTypeName.get(RXACTIONDELEGATE_RESULT_TYPE, actionDelegateType));
                        ParameterizedTypeName rxSubscribeActionType = ParameterizedTypeName.get(RXACTIONDELEGATE_SUBSCRIBEACTION_TYPE, actionDelegateType);


                        VariableElement delegateVariable = parameters.get(delegateIndex);
                        TypeSpec onSubscribeActionClass = TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(rxSubscribeActionType)
                                .addMethod(
                                        MethodSpec.methodBuilder("subscribe")
                                                .addAnnotation(Override.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .returns(TypeName.VOID)
                                                .addParameter(ClassName.get(delegateVariable.asType()), delegateVariable.toString())
                                                .addStatement("$L.$L($L)", mTargetVar, method.getSimpleName(), wrapArgStr)
                                                .build()
                                ).build();

                        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                                .addModifiers(Modifier.PUBLIC)
                                .addParameters(newParameters)
                                .returns(single ? singleType : observableType)
                                .addStatement("return $T.$L($L)", RXACTIONDELEGATE_TYPE, single ? "single" : "observable", onSubscribeActionClass);

                        modelBuilder.addMethod(methodBuilder.build());
                    }

                    TypeName returnTypeName = TypeName.get(method.getReturnType());
                    boolean returnValue = !returnTypeName.equals(TypeName.VOID);

                    List<ParameterSpec> parameterSpecs = new ArrayList<>();
                    String parameters = "";
                    for (int i = 0; i < method.getParameters().size(); i++) {
                        VariableElement variableElement = method.getParameters().get(i);
                        parameterSpecs.add(ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build());
                        parameters += variableElement.getSimpleName();
                        if (i < method.getParameters().size() - 1) {
                            parameters += ", ";
                        }

                    }


                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .addParameters(parameterSpecs)
                            .returns(returnTypeName);
                    methodBuilder.addStatement("$L$L.$L($L)", returnValue ? "return " : "", mTargetVar, method.getSimpleName(), parameters);


                    modelBuilder.addMethod(methodBuilder.build());

                }
                saveClass(processingEnvironment, className.packageName(), modelBuilder);


                ClassName.get("com.leroymerlin.pandroid.mvvm", "ObservableString");
            }

        }
    }

    private boolean checkRxEnabled(ProcessingEnvironment processingEnvironment) {
        return processingEnvironment.getElementUtils().getTypeElement(OBSERVABLE_TYPE.toString()) != null;
    }

    @Override
    public boolean useGeneratedAnnotation() {
        return true;
    }
}
