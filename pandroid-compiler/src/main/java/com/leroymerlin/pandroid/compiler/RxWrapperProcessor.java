package com.leroymerlin.pandroid.compiler;

import com.google.common.collect.Lists;
import com.leroymerlin.pandroid.annotations.RxModel;
import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.future.ActionDelegate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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
    private static final ClassName COMPLETABLE_TYPE = ClassName.get("io.reactivex", "Completable");
    private static final ClassName RXACTIONDELEGATE_TYPE = ClassName.get("com.leroymerlin.pandroid.future", "RxActionDelegate");
    private static final ClassName RXACTIONDELEGATE_SUBSCRIBEACTION_TYPE = RXACTIONDELEGATE_TYPE.nestedClass("OnSubscribeAction");
    private static final ClassName RXACTIONDELEGATE_RESULT_TYPE = RXACTIONDELEGATE_TYPE.nestedClass("Result");
    private TypeMirror ACTIONDELEGATE_TYPE;

    private static final ClassName BOXED_VOID = ClassName.get("java.lang", "Void");



    private static final String TARGET_VAR = "mTarget";


    RxWrapperProcessor(Elements elements, Types types) {
        super(elements, types);

    }

    @Override
    public List<String> getSupportedAnnotations() {
        return Lists.newArrayList(RxWrapper.class.getCanonicalName());
    }


    @Override
    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
        if (!checkRxEnabled(processingEnvironment)) return;

        ACTIONDELEGATE_TYPE = mElementsUtils.getTypeElement(ActionDelegate.class.getCanonicalName()).asType();

        Set<? extends Element> models = roundEnvironment.getElementsAnnotatedWith
                (RxModel.class);
        List<ExecutableElement> modelsElements = new ArrayList<>();

        for (Element element : models) {
            ExecutableElement executableElement = (ExecutableElement) element;
            if (!executableElement.getModifiers().contains(Modifier.STATIC)) {
                throw new IllegalStateException("RxModel only support static method");
            }
            modelsElements.add(executableElement);
        }

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith
                (RxWrapper.class);
        List<TypeElement> classesToProcess = new ArrayList<>();

        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = null;
            if (element instanceof ExecutableElement) {
                typeElement = (TypeElement) element.getEnclosingElement();
            } else {
                typeElement = (TypeElement) element;
            }
            if (!classesToProcess.contains(typeElement)) {
                classesToProcess.add(typeElement);
            }
        }

        for (TypeElement classType : classesToProcess) {
            ClassName className = ClassName.get(classType);
            boolean inInterface = classType.getKind().isInterface();
            List<TypeVariableName> typeVariableNames = new ArrayList<>();
            for (TypeParameterElement element : classType.getTypeParameters()) {
                typeVariableNames.add(TypeVariableName.get(element));
            }

            ArrayList<Modifier> modifiers = new ArrayList<>(classType.getModifiers());
            if (inInterface) {
                modifiers.remove(Modifier.ABSTRACT);
            }

            TypeSpec.Builder modelBuilder = TypeSpec.classBuilder("Rx" + ProcessorUtils.capitalize(className.simpleName()))
                    .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]))
                    .addTypeVariables(typeVariableNames);

            for (ExecutableElement element : modelsElements) {
                AnnotationMirror annotation = ProcessorUtils.getAnnotationMirror(element, RxModel.class);
                List<AnnotationValue> targetsType = ((List<AnnotationValue>) ProcessorUtils.getAnnotationValue(annotation, "targets"));

                boolean match = false;
                for (AnnotationValue c : targetsType) {
                    TypeMirror matchType = (TypeMirror) c.getValue();
                    if (mTypesUtils.isAssignable(mTypesUtils.erasure(classType.asType()), matchType)) {
                        match = true;
                        break;
                    }

                }
                if (match) {

                    TypeMirror returnTypeMirror = element.getReturnType();
                    TypeName returnType = TypeName.get(returnTypeMirror);


                    MethodSpec.Builder builder = MethodSpec.methodBuilder(element.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(returnType);

                    for (TypeParameterElement parameterElement : element.getTypeParameters()) {
                        builder.addTypeVariable(TypeVariableName.get(parameterElement.getSimpleName().toString()));
                    }
                    String parameters = "";

                    for (int i = 0; i < element.getParameters().size(); i++) {
                        VariableElement variableElement = element.getParameters().get(i);

                        String renamedParam = null;
                        for (AnnotationValue c : targetsType) {
                            TypeMirror matchType = (TypeMirror) c.getValue();

                            if (mTypesUtils.isAssignable(mTypesUtils.erasure(variableElement.asType()), matchType)) {
                                renamedParam = "this";
                            }
                        }
                        if (renamedParam == null) {
                            renamedParam = variableElement.getSimpleName().toString();
                            builder.addParameter(ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build());
                        }

                        boolean wasEmpty = parameters.isEmpty();
                        parameters = parameters + (wasEmpty ? "" : ", ") + renamedParam;

                    }
                    boolean returnValue = !returnType.box().equals(BOXED_VOID);

                    builder.addStatement("$L$T.$L($L)", returnValue ? "return " : "", element.getEnclosingElement(), element.getSimpleName().toString(), parameters);
                    modelBuilder.addMethod(builder.build());
                }
            }

            if (inInterface) {
                modelBuilder.addSuperinterface(TypeName.get(classType.asType()))
                        .addField(className, TARGET_VAR, Modifier.PRIVATE, Modifier.FINAL);
                //add contructor
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(
                                className.packageName(),
                                className.simpleName()
                        ), "target")
                        .addStatement("this.$L = $L", TARGET_VAR, "target");

                modelBuilder.addMethod(constructorBuilder.build());

                List<ExecutableElement> methodToImplement = new ArrayList<>(ElementFilter.methodsIn(classType.getEnclosedElements()));
                for (TypeMirror interfaceMirror : classType.getInterfaces()) {
                    List<ExecutableElement> methods = ElementFilter.methodsIn(mTypesUtils.asElement(interfaceMirror).getEnclosedElements());
                    for (ExecutableElement newMethod : methods) {
                        boolean addMethod = true;
                        for (ExecutableElement m : methodToImplement) {
                            if (ProcessorUtils.sameMethods(mTypesUtils, m, newMethod)) {
                                addMethod = false;
                                break;
                            }
                        }
                        if (addMethod) {
                            methodToImplement.add(newMethod);
                        }
                    }
                }

                for (ExecutableElement m : methodToImplement) {
                    wrapInterfaceMethod(modelBuilder, m);
                }

            } else {
                modelBuilder.superclass(TypeName.get(classType.asType()));
                for (ExecutableElement method : ElementFilter.constructorsIn(classType.getEnclosedElements())) {
                    if (!method.getModifiers().contains(Modifier.PRIVATE)) {

                        MethodData methodData = new MethodData(method);
                        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                                .addModifiers(method.getModifiers())
                                .addParameters(methodData.parameterSpecs)
                                .addStatement("super($L)", toParameterStr(methodData.parameterSpecs));
                        modelBuilder.addMethod(constructorBuilder.build());

                    }
                }
            }


            for (ExecutableElement method : ElementFilter.methodsIn(classType.getEnclosedElements())) {

                MethodData methodData = new MethodData(method);


                RxWrapper rxWrapperAnnotation = method.getAnnotation(RxWrapper.class);
                if (rxWrapperAnnotation != null) {

                    TypeName baseReturnType = methodData.returnType;
                    TypeName returnType = rxWrapperAnnotation.wrapResult() ? ParameterizedTypeName.get(RXACTIONDELEGATE_RESULT_TYPE, baseReturnType) : baseReturnType;

                    boolean singleValue = rxWrapperAnnotation.singleValue();
                    boolean returnVoid = returnType.box().equals(BOXED_VOID);

                    TypeName rxType;
                    TypeName rxTypeWithParameters;
                    String rxTypeMethodName;
                    if (singleValue && returnVoid) {
                        rxType = COMPLETABLE_TYPE;
                        rxTypeWithParameters = COMPLETABLE_TYPE;
                        rxTypeMethodName = "completable";
                        if (rxWrapperAnnotation.wrapResult()) {
                            throw new IllegalStateException("Can't wrap result of method with Void as return type." + className.toString() + ":" + method.getSimpleName().toString());
                        }
                    } else if (singleValue) {
                        rxType = SINGLE_TYPE;
                        rxTypeWithParameters = ParameterizedTypeName.get(SINGLE_TYPE, returnType);
                        rxTypeMethodName = "single" + (rxWrapperAnnotation.wrapResult() ? "Wrapped" : "");
                    } else {
                        rxType = OBSERVABLE_TYPE;
                        rxTypeWithParameters = ParameterizedTypeName.get(OBSERVABLE_TYPE, returnType);
                        rxTypeMethodName = "observable" + (rxWrapperAnnotation.wrapResult() ? "Wrapped" : "");
                    }

                    if (methodData.delegateParameter != null) { //Action delegate replacement

                        ParameterizedTypeName rxSubscribeActionType = ParameterizedTypeName.get(RXACTIONDELEGATE_SUBSCRIBEACTION_TYPE, baseReturnType);


                        TypeSpec onSubscribeActionClass = TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(rxSubscribeActionType)
                                .addMethod(
                                        MethodSpec.methodBuilder("subscribe")
                                                .addAnnotation(Override.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .returns(TypeName.VOID)
                                                .addParameter(methodData.delegateParameter)
                                                .addStatement("$L($L)", method.getSimpleName(), toParameterStr(methodData.parameterSpecs))
                                                .build()
                                ).build();

                        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(createWrapMethodName(method.getSimpleName().toString()))
                                .addModifiers(Modifier.PUBLIC)
                                .addExceptions(methodData.exceptions)
                                .addTypeVariables(methodData.typeVariableNames)
                                .addParameters(methodData.parameterSpecsWithoutDelegate)
                                .returns(rxTypeWithParameters)
                                .addStatement("return $T.$L($L)", RXACTIONDELEGATE_TYPE, rxTypeMethodName, onSubscribeActionClass);

                        modelBuilder.addMethod(methodBuilder.build());
                    } else { //method replacement
                        if (!rxWrapperAnnotation.singleValue()) {
                            throw new IllegalStateException("Can't wrap a method as Observable. Please remove single or transform method with an ActionDelegate. " + className.toString() + ":" + method.getSimpleName().toString());
                        }


                        MethodSpec.Builder callMethodBuilder = MethodSpec.methodBuilder("call")
                                .addAnnotation(Override.class)
                                .addException(Exception.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeName.OBJECT);
                        if(returnVoid){
                            callMethodBuilder.addStatement("$L($L)", method.getSimpleName(), toParameterStr(methodData.parameterSpecs));
                            callMethodBuilder.addStatement("return null");
                        }else if (rxWrapperAnnotation.wrapResult()) {
                            callMethodBuilder.addStatement("return ($T) $T.wrap($L($L))", returnType, RXACTIONDELEGATE_TYPE, method.getSimpleName(), toParameterStr(methodData.parameterSpecs));
                        } else {
                            callMethodBuilder.addStatement("return ($T) $L($L)", returnType, method.getSimpleName(), toParameterStr(methodData.parameterSpecs));

                        }
                        TypeSpec callableClass = TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(CALLABLE_TYPE)
                                .addMethod(callMethodBuilder.build()).build();


                        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(createWrapMethodName(method.getSimpleName().toString()))
                                .addModifiers(Modifier.PUBLIC)
                                .addTypeVariables(methodData.typeVariableNames)
                                .addParameters(methodData.parameterSpecs)
                                .returns(rxTypeWithParameters)
                                .addStatement("return $T.fromCallable($L)", rxType, callableClass);

                        modelBuilder.addMethod(methodBuilder.build());

                    }
                }
            }
            saveClass(processingEnvironment, className.packageName(), modelBuilder);
        }
    }

    private void wrapInterfaceMethod(TypeSpec.Builder modelBuilder, ExecutableElement method) {
        MethodData methodData = new MethodData(method);
        TypeName returnTypeName = TypeName.get(method.getReturnType());
        boolean returnValue = !returnTypeName.box().equals(BOXED_VOID);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addExceptions(methodData.exceptions)
                .addParameters(methodData.parameterSpecs)
                .addTypeVariables(methodData.typeVariableNames)
                .returns(returnTypeName);
        methodBuilder.addStatement("$L$L.$L($L)", returnValue ? "return " : "", TARGET_VAR, method.getSimpleName(), toParameterStr(methodData.parameterSpecs));
        modelBuilder.addMethod(methodBuilder.build());
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
        List<TypeVariableName> typeVariableNames = new ArrayList<>();
        List<ParameterSpec> parameterSpecsWithoutDelegate = new ArrayList<>();
        List<TypeName> exceptions = new ArrayList<>();

        MethodData(ExecutableElement method) {

            for (TypeParameterElement element : method.getTypeParameters()) {
                typeVariableNames.add(TypeVariableName.get(element));
            }

            for (TypeMirror element : method.getThrownTypes()) {
                exceptions.add(TypeName.get(element));
            }


            for (int i = 0; i < method.getParameters().size(); i++) {
                VariableElement variableElement = method.getParameters().get(i);
                ParameterSpec.Builder parameterSpecBuilder = ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString());
                parameterSpecBuilder.addModifiers(Modifier.FINAL);

                parameterSpecs.add(parameterSpecBuilder.build());
                if (returnType == null && mTypesUtils.isAssignable(mTypesUtils.erasure(variableElement.asType()), ACTIONDELEGATE_TYPE)) {
                    if (!mTypesUtils.erasure(variableElement.asType()).equals(mTypesUtils.erasure(ACTIONDELEGATE_TYPE))) {
                        throw new IllegalStateException("Can't wrap a method with a class with parameter type " + mTypesUtils.erasure(variableElement.asType()) + ". Please use ActionDelegate instead in " + method.toString());
                    }

                    TypeName typeName = ParameterizedTypeName.get(variableElement.asType());
                    if (typeName instanceof ParameterizedTypeName) {
                        returnType = ((ParameterizedTypeName) typeName).typeArguments.get(0);
                    } else { // action delegate with no ParameterizedTypeName
                        returnType = TypeName.OBJECT;
                    }
                    delegateParameter = ParameterSpec.builder(ClassName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build();
                } else {
                    parameterSpecsWithoutDelegate.add(parameterSpecBuilder.build());
                }
            }

            if (returnType == null) {
                returnType = TypeName.get(method.getReturnType());
            }
        }


    }

    static String toParameterStr(List<ParameterSpec> params) {
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
