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
import java.util.List;
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
 * <p>
 * Generate class to wrap rxWrapped method
 */

class RxWrapperProcessor extends BaseProcessor {


    private static final ClassName CALLABLE_TYPE = ClassName.get("java.util.concurrent", "Callable");
    private static final ClassName SINGLE_TYPE = ClassName.get("io.reactivex", "Single");
    private static final ClassName OBSERVABLE_TYPE = ClassName.get("io.reactivex", "Observable");
    private static final ClassName RXACTIONDELEGATE_TYPE = ClassName.get("com.leroymerlin.pandroid.future", "RxActionDelegate");
    private static final ClassName RXACTIONDELEGATE_SUBSCRIBEACTION_TYPE = RXACTIONDELEGATE_TYPE.nestedClass("OnSubscribeAction");
    private static final ClassName RXACTIONDELEGATE_RESULT_TYPE = RXACTIONDELEGATE_TYPE.nestedClass("Result");
    private TypeMirror ACTIONDELEGATE_TYPE;


    RxWrapperProcessor(Elements elements, Types types) {
        super(elements, types);
        ACTIONDELEGATE_TYPE = mElementsUtils.getTypeElement(ActionDelegate.class.getCanonicalName()).asType();

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

        for (TypeElement classType : classesToProcess) {
            ClassName className = ClassName.get(classType);
            boolean inInterface = classType.getKind().isInterface();
            String mTargetVar = "mTarget";
            TypeSpec.Builder modelBuilder = TypeSpec.classBuilder("Rx" + ProcessorUtils.capitalize(className.simpleName()))
                    .addModifiers(classType.getModifiers().toArray(new Modifier[classType.getModifiers().size()]));
            if (inInterface) {
                modelBuilder.addSuperinterface(className)
                        .addField(className, mTargetVar, Modifier.PRIVATE, Modifier.FINAL);
                //add contructor
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(
                                className.packageName(),
                                className.simpleName()
                        ), "target")
                        .addStatement("this.$L = $L", mTargetVar, "target");

                modelBuilder.addMethod(constructorBuilder.build());
            } else {
                modelBuilder.superclass(className);
                for (ExecutableElement method : ElementFilter.constructorsIn(classType.getEnclosedElements())) {
                    if (!method.getModifiers().contains(Modifier.PRIVATE)) {

                        MethodData methodData = new MethodData(method);
                        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                                .addModifiers(method.getModifiers())
                                .addParameters(methodData.parameterSpecs)
                                .addStatement("super($L)", methodData.toParameterStr(methodData.parameterSpecs));
                        modelBuilder.addMethod(constructorBuilder.build());

                    }
                }
            }


            for (ExecutableElement method : ElementFilter.methodsIn(classType.getEnclosedElements())) {

                MethodData methodData = new MethodData(method);


                RxWrapper rxWrapperAnnotation = method.getAnnotation(RxWrapper.class);
                if (rxWrapperAnnotation != null) {

                    boolean single = !rxWrapperAnnotation.stream();
                    boolean returnVoid = methodData.returnType.equals(TypeName.VOID);
                    if (returnVoid && methodData.delegateParameter == null) {
                        throw new IllegalStateException("Method should have an ActionDelegate type in the parameters to be RxWrapped. " + className.toString() + ":" + method.getSimpleName().toString());
                    }

                    ParameterizedTypeName singleType = ParameterizedTypeName.get(SINGLE_TYPE, methodData.returnType);

                    if (methodData.delegateParameter != null) { //Action delegate replacement

                        ParameterizedTypeName observableType = ParameterizedTypeName.get(OBSERVABLE_TYPE, ParameterizedTypeName.get(RXACTIONDELEGATE_RESULT_TYPE, methodData.returnType));
                        ParameterizedTypeName rxSubscribeActionType = ParameterizedTypeName.get(RXACTIONDELEGATE_SUBSCRIBEACTION_TYPE, methodData.returnType);


                        TypeSpec onSubscribeActionClass = TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(rxSubscribeActionType)
                                .addMethod(
                                        MethodSpec.methodBuilder("subscribe")
                                                .addAnnotation(Override.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .returns(TypeName.VOID)
                                                .addParameter(methodData.delegateParameter)
                                                .addStatement("$L($L)", method.getSimpleName(), methodData.toParameterStr(methodData.parameterSpecs))
                                                .build()
                                ).build();

                        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(createWrapMethodName(method.getSimpleName().toString()))
                                .addModifiers(Modifier.PUBLIC)
                                .addParameters(methodData.parameterSpecsWithoutDelegate)
                                .returns(single ? singleType : observableType)
                                .addStatement("return $T.$L($L)", RXACTIONDELEGATE_TYPE, single ? "single" : "observable", onSubscribeActionClass);

                        modelBuilder.addMethod(methodBuilder.build());
                    } else { //method replacement
                        if (rxWrapperAnnotation.stream()) {
                            throw new IllegalStateException("Can't wrappe a method as Observable. Please remove stream or transform method with an ActionDelegate. " + className.toString() + ":" + method.getSimpleName().toString());
                        }

                        ParameterizedTypeName callableType = ParameterizedTypeName.get(CALLABLE_TYPE, methodData.returnType);

                        TypeSpec callableClass = TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(callableType)
                                .addMethod(
                                        MethodSpec.methodBuilder("call")
                                                .addAnnotation(Override.class)
                                                .addException(Exception.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .returns(methodData.returnType)
                                                .addStatement("return $L($L)", method.getSimpleName(), methodData.toParameterStr(methodData.parameterSpecs))
                                                .build()
                                ).build();


                        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(createWrapMethodName(method.getSimpleName().toString()))
                                .addModifiers(Modifier.PUBLIC)
                                .addParameters(methodData.parameterSpecs)
                                .returns(singleType)
                                .addStatement("return $T.fromCallable($L)", SINGLE_TYPE, callableClass);

                        modelBuilder.addMethod(methodBuilder.build());

                    }
                }


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

                if (inInterface) {
                    TypeName returnTypeName = TypeName.get(method.getReturnType());
                    boolean returnValue = !returnTypeName.equals(TypeName.VOID);


                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .addParameters(parameterSpecs)
                            .returns(returnTypeName);
                    methodBuilder.addStatement("$L$L.$L($L)", returnValue ? "return " : "", mTargetVar, method.getSimpleName(), parameters);
                    modelBuilder.addMethod(methodBuilder.build());
                }

            }
            saveClass(processingEnvironment, className.packageName(), modelBuilder);
        }
    }

    private boolean checkRxEnabled(ProcessingEnvironment processingEnvironment) {
        return processingEnvironment.getElementUtils().getTypeElement(OBSERVABLE_TYPE.toString()) != null;
    }


    private String createWrapMethodName(String methodName) {
        return "rx" + ProcessorUtils.capitalize(methodName);
    }

    @Override
    public boolean useGeneratedAnnotation() {
        return true;
    }


    private class MethodData {

        TypeName returnType;
        ParameterSpec delegateParameter;
        List<ParameterSpec> parameterSpecs = new ArrayList<>();
        List<ParameterSpec> parameterSpecsWithoutDelegate = new ArrayList<>();

        MethodData(ExecutableElement method) {
            for (int i = 0; i < method.getParameters().size(); i++) {
                VariableElement variableElement = method.getParameters().get(i);
                ParameterSpec.Builder parameterSpecBuilder = ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString());
                parameterSpecBuilder.addModifiers(Modifier.FINAL);

                parameterSpecs.add(parameterSpecBuilder.build());
                if (returnType == null && mTypesUtils.isAssignable(mTypesUtils.erasure(variableElement.asType()), ACTIONDELEGATE_TYPE)) {
                    returnType = ((ParameterizedTypeName) ParameterizedTypeName.get(variableElement.asType())).typeArguments.get(0);
                    delegateParameter = ParameterSpec.builder(ClassName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build();
                } else {
                    parameterSpecsWithoutDelegate.add(parameterSpecBuilder.build());
                }
            }

            if (returnType == null) {
                returnType = TypeName.get(method.getReturnType());
            }
        }


        String toParameterStr(List<ParameterSpec> params) {
            String result = "";
            for (int i = 0; i < params.size(); i++) {
                ParameterSpec s = params.get(i);
                result += s.name;
                if (i < params.size() - 1) {
                    result += ", ";
                }
            }
            return result;
        }

    }
}
