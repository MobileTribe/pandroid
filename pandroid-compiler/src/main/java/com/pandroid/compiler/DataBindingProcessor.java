package com.pandroid.compiler;

import com.pandroid.annotations.DataBinding;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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
import javax.tools.Diagnostic;

/**
 * Created by adrien le roy on 29/09/2016.
 */

public class DataBindingProcessor {

    private final Elements mElementsUtils;

    HashMap<ClassName, ClassName> observableClasses = new HashMap<ClassName, ClassName>() {{
        put(ClassName.get(String.class), ClassName.get("com.leroymerlin.pandroid.mvvm", "ObservableString"));
        put(ClassName.get(Short.class), ClassName.get("android.databinding", "ObservableShort"));
        put(ClassName.get(Integer.class), ClassName.get("android.databinding", "ObservableInt"));
        put(ClassName.get(Float.class), ClassName.get("android.databinding", "ObservableFloat"));
        put(ClassName.get(Long.class), ClassName.get("android.databinding", "ObservableLong"));
        put(ClassName.get(Double.class), ClassName.get("android.databinding", "ObservableDouble"));
        put(ClassName.get(Character.class), ClassName.get("android.databinding", "ObservableChar"));
        put(ClassName.get(Boolean.class), ClassName.get("android.databinding", "ObservableBoolean"));
        put(ClassName.get(Byte.class), ClassName.get("android.databinding", "ObservableByte"));
        put(ClassName.get("android.os", "Parcelable"), ClassName.get("android.databinding", "ObservableParcelable"));
        put(ClassName.get(Object.class), ClassName.get("android.databinding", "ObservableField"));

        //put(Map.class, ObservableArrayMap.class);
        //put(List.class, ObservableArrayList.class);
    }};

    /*.asList(
            ClassName.get("com.leroymerlin.pandroid.mvvm", "ObservableString"),
            ClassName.get("android.databinding", "ObservableInt"),
            ClassName.get("android.databinding", "ObservableLong")
    );*/
    private final ClassName observableClass = ClassName.get("android.databinding", "Observable");
    private final ClassName observableCallbackClass = ClassName.get("android.databinding", "Observable.OnPropertyChangedCallback");

    private final ClassName DataBindingModelWrapperClass = ClassName.get("com.leroymerlin.pandroid.mvvm", "DataBindingModelWrapper");
    private ProcessingEnvironment processingEnvironment;

    public DataBindingProcessor(Elements elements) {
        mElementsUtils = elements;
    }


    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(DataBinding.class);
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            ClassName modelClassName = ClassName.get(typeElement);


            String model = firstLetterLowercase(modelClassName.simpleName());
            String finalClassName = modelClassName.simpleName() + "DataBinding";

            //on creer la class
            TypeSpec.Builder databindingClassBuilder = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC).superclass(DataBindingModelWrapperClass);

            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);


            MethodSpec.Builder setModelMethodBuilder = MethodSpec.methodBuilder("set" + capitalize(model)).addModifiers(Modifier.PUBLIC).addParameter(modelClassName, model);
            setModelMethodBuilder.addStatement("this.$L = $L", model, model)
                    .beginControlFlow("if($L != null)", model);


            String mapGetParam = "key";
            MethodSpec.Builder mapGetMethodBuilder = MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Object.class, mapGetParam)
                    .addAnnotation(Override.class)
                    .returns(Object.class);


            MethodSpec.Builder getModelMethodBuilder = MethodSpec.methodBuilder("get").addModifiers(Modifier.PUBLIC).returns(modelClassName);
            getModelMethodBuilder.addStatement("return this.$L", model);
            databindingClassBuilder.addMethod(getModelMethodBuilder.build());

            List<FieldSpec> fieldSpecList = new ArrayList<>();

            //champs qui contient le model
            fieldSpecList.add(FieldSpec.builder(modelClassName, model, Modifier.PRIVATE).build());

            //on recupere les champs a ajouter
            List<VariableElement> elements = ElementFilter.fieldsIn(typeElement.getEnclosedElements());
            for (VariableElement variableElement : elements) {
                TypeMirror fieldType = variableElement.asType();
                String fullTypeClassName = fieldType.toString();

                String fieldName = variableElement.getSimpleName().toString();

                ClassName fieldClassName = findFieldType(fieldType);
                if (fieldClassName == null) {
                    log(processingEnvironment, "No Bindable Object available for " + fullTypeClassName, Diagnostic.Kind.MANDATORY_WARNING);
                } else {

                    fieldSpecList.add(FieldSpec.builder(fieldClassName, fieldName, Modifier.PUBLIC)
                            .initializer("new $T()", fieldClassName)
                            .build());
                    setModelMethodBuilder.addStatement(fieldName + ".set($L)", formatGetValue(model, typeElement, variableElement));


                    TypeSpec propertyChangedCallback = TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(observableCallbackClass)
                            .addMethod(
                                    MethodSpec.methodBuilder("onPropertyChanged")
                                            .addParameter(observableClass, "observable")
                                            .addParameter(TypeName.INT, "i")
                                            .addAnnotation(Override.class)
                                            .addModifiers(Modifier.PUBLIC)
                                            .beginControlFlow("if($L != null)", model)
                                            .addStatement(formatSetValue(model, typeElement, variableElement), "(" + variableElement.asType() + ")" + fieldName + ".get()")
                                            .endControlFlow()
                                            .addStatement("notifyChange($S)", fieldName)
                                            .build())
                            .build();
                    constructorMethodBuilder.addStatement("$L.addOnPropertyChangedCallback($L)", fieldName, propertyChangedCallback);


                    mapGetMethodBuilder.addStatement("if($S.equals($L)) return this.$L", fieldName, mapGetParam, fieldName);
                    /*MethodSpec.Builder overrideFieldSetter = MethodSpec.methodBuilder("set" + capitalize(fieldName))
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addParameter(ClassName.get(fieldType), fieldName)
                            .addStatement("this.$L.set($L)", fieldName, fieldName);

                    databindingClassBuilder.addMethod(overrideFieldSetter.build());

                    MethodSpec.Builder overrideFieldGetter = MethodSpec.methodBuilder("get" + capitalize(fieldName))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ClassName.get(Object.class))
                            .addStatement("return this.$L.get()", fieldName);
                    databindingClassBuilder.addMethod(overrideFieldGetter.build());
*/

                }

            }
            setModelMethodBuilder.endControlFlow();
            databindingClassBuilder.addMethod(setModelMethodBuilder.build());

            mapGetMethodBuilder.addStatement("return null");
            databindingClassBuilder.addMethod(mapGetMethodBuilder.build());
            databindingClassBuilder.addFields(fieldSpecList);
            databindingClassBuilder.addMethod(constructorMethodBuilder.build());

            //on enregistre la class
            JavaFile javaFile = JavaFile.builder(modelClassName.packageName(), databindingClassBuilder.build())
                    .build();
            javaFile.toJavaFileObject();
            try {
                javaFile.writeTo(processingEnvironment.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ClassName findFieldType(TypeMirror fieldType) {
        if (fieldType == null)
            return null;
        for (Map.Entry<ClassName, ClassName> observableClass : observableClasses.entrySet()) {
            if (ClassName.get(fieldType).box().equals(observableClass.getKey())) {
                return observableClass.getValue();
            }
        }
        return findFieldType(mElementsUtils.getTypeElement(fieldType.toString()).getSuperclass());
    }

    private String formatSetValue(String modelName, TypeElement typeElement, VariableElement variableElement) {
        String fieldName = variableElement.getSimpleName().toString();
        if (variableElement.getModifiers().contains(Modifier.PUBLIC))
            return modelName + "." + fieldName + " = $L";
        String capitalized = capitalize(fieldName);
        String[] methodNames = new String[]{"set" + capitalized, fieldName};
        for (String methodName : methodNames) {
            List<ExecutableElement> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements());
            for (ExecutableElement method : methods) {
                if (method.getSimpleName().contentEquals(methodName) && method.getModifiers().contains(Modifier.PUBLIC) && !method.getParameters().isEmpty() && method.getParameters().get(0).asType().equals(variableElement.asType())) {
                    return modelName + "." + method.getSimpleName() + "($L)";
                }
            }
        }
        log(processingEnvironment, "Can't access " + variableElement.getSimpleName() + " field in " + typeElement.getSimpleName() + ".  Add a setter or make the field public", Diagnostic.Kind.ERROR);
        return null;
    }

    private String formatGetValue(String modelName, TypeElement typeElement, VariableElement variableElement) {
        String fieldName = variableElement.getSimpleName().toString();
        if (variableElement.getModifiers().contains(Modifier.PUBLIC))
            return modelName + "." + fieldName;
        String capitalized = capitalize(fieldName);
        String[] methodNames = new String[]{"get" + capitalized, "is" + capitalized, fieldName};
        for (String methodName : methodNames) {
            List<ExecutableElement> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements());
            for (ExecutableElement method : methods) {
                if (method.getSimpleName().contentEquals(methodName) && method.getModifiers().contains(Modifier.PUBLIC) && !method.isVarArgs()) {
                    return modelName + "." + method.getSimpleName() + "()";
                }
            }
        }
        log(processingEnvironment, "Can't access " + variableElement.getSimpleName() + " field in " + typeElement.getSimpleName() + ".  Add a getter or make the field public", Diagnostic.Kind.ERROR);
        return null;
    }

    private void log(ProcessingEnvironment environment, String msg, Diagnostic.Kind level) {
        environment.getMessager().printMessage(level, msg);
    }

    private String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String firstLetterLowercase(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

}
