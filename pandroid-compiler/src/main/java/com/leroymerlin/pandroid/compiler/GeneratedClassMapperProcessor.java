package com.leroymerlin.pandroid.compiler;

import com.google.common.collect.Lists;
import com.leroymerlin.pandroid.annotations.PandroidGeneratedClass;
import com.leroymerlin.pandroid.app.PandroidMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by florian on 30/11/15.
 */
public class GeneratedClassMapperProcessor extends BaseProcessor {


    private boolean fileSaved;

    public GeneratedClassMapperProcessor(Elements elements) {
        super(elements);
    }

    @Override
    public List<String> getSupportedAnnotations() {
        return Lists.newArrayList(PandroidGeneratedClass.class.getCanonicalName());
    }

    Map<DeclaredType, Map<DeclaredType, TypeElement>> dataMap = new HashMap<>();

    @Override
    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {


        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(PandroidGeneratedClass.class);


        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            ClassName modelClassName = ClassName.get(typeElement);


            DeclaredType type = null;
            DeclaredType target = null;
            List<? extends AnnotationMirror> annotations = typeElement.getAnnotationMirrors();
            for (AnnotationMirror annotation : annotations) {
                DeclaredType annotationType = annotation.getAnnotationType();
                if (annotationType.toString().equals(PandroidGeneratedClass.class.getName())) {

                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> value : annotation.getElementValues().entrySet()) {
                        if ("target()".equals(value.getKey().toString())) {
                            target = (DeclaredType) value.getValue().getValue();
                        } else if ("type()".equals(value.getKey().toString())) {
                            type = (DeclaredType) value.getValue().getValue();
                        }
                    }
                    break;
                }
            }

            if (type != null && target != null) {
                if (!dataMap.containsKey(type)) {
                    dataMap.put(type, new HashMap<DeclaredType, TypeElement>());
                }
                dataMap.get(type).put(target, typeElement);
            }
        }

        if (!fileSaved && !isClassGenerated() && !roundEnvironment.processingOver()) {

            TypeElement pandroidMapperImplType = processingEnvironment.getElementUtils().getTypeElement(PandroidMapper.MAPPER_PACKAGE + "." + PandroidMapper.MAPPER_IMPL_NAME);

            if (pandroidMapperImplType == null) {
                //We probably are in a android library
                return;
            }

            String packageName = null;
            for (Element element : pandroidMapperImplType.getEnclosedElements()) {
                if (PandroidMapper.PACKAGE_ATTR.equals(element.getSimpleName().toString())) {
                    packageName = (String) ((VariableElement) element).getConstantValue();
                    break;
                }
            }

            if (packageName == null) {
                log(processingEnvironment, "Can't find packageName", Diagnostic.Kind.ERROR);
            }

            TypeSpec.Builder wrapperBuilder = TypeSpec.classBuilder(PandroidMapper.WRAPPER_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            TypeVariableName t = TypeVariableName.get("T");
            ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(List.class), t);
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(PandroidMapper.WRAPPER_METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addTypeVariable(t)
                    .returns(returnType)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), t), "type")
                    .addParameter(TypeName.OBJECT, "target")

                    .addStatement("$T result = new $T()", returnType, ParameterizedTypeName.get(ClassName.get(ArrayList.class), t));

            CodeBlock.Builder codeBuilder = CodeBlock.builder();
            for (Map.Entry<DeclaredType, Map<DeclaredType, TypeElement>> entry : dataMap.entrySet()) {
                codeBuilder.beginControlFlow("if(type.equals($T.class))", entry.getKey());
                for (Map.Entry<DeclaredType, TypeElement> content : entry.getValue().entrySet()) {
                    codeBuilder.beginControlFlow("if(target.getClass().isAssignableFrom($T.class))", content.getKey());
                    TypeElement generatedClass = content.getValue();
                    for (Element enclosed : generatedClass.getEnclosedElements()) {
                        if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                            ExecutableElement constructorElement = (ExecutableElement) enclosed;
                            if (constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                                if (constructorElement.getParameters().size() == 0) {
                                    codeBuilder.addStatement("result.add(($T)new $T())", t, generatedClass);
                                    break;
                                } else if (constructorElement.getParameters().size() == 1) {
                                    codeBuilder.addStatement("result.add(($T)new $T(($T)target))", t, generatedClass, constructorElement.getParameters().get(0).asType());
                                    break;
                                }
                            }
                        }
                    }
                    codeBuilder.endControlFlow();
                }
                codeBuilder.endControlFlow();
            }

            methodBuilder.addCode(codeBuilder.build());
            methodBuilder.addStatement("return result");
            wrapperBuilder.addMethod(methodBuilder.build());


            saveClass(processingEnvironment, packageName, wrapperBuilder);
            fileSaved = true;
        }
/*

            TypeSpec.Builder databindingClassBuilder = TypeSpec.classBuilder(packageName+"."+)
                    .addModifiers(Modifier.PUBLIC);
                    */

/*
            boolean twoway = typeElement.getAnnotation(DataBinding.class).twoway();
            String model = modelClassName.simpleName().toLowerCase();
            String finalClassName = modelClassName.simpleName() + "DataBinding";

            //on creer la class
            TypeSpec.Builder databindingClassBuilder = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC);

            MethodSpec.Builder setModelMethodBuilder = MethodSpec.methodBuilder("set" + capitalize(model)).addModifiers(Modifier.PUBLIC).addParameter(modelClassName, model);
            setModelMethodBuilder.addStatement("this.$L = $L", model, model);


            MethodSpec.Builder getModelMethodBuilder = MethodSpec.methodBuilder("get" + capitalize(model)).addModifiers(Modifier.PUBLIC).returns(modelClassName);
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
                    setModelMethodBuilder.addStatement(fieldName + ".set($L.get$L())", model, capitalize(fieldName));

                    //si on fait un binding twoway
                    if (twoway) {
                        String setter = variableElement.getSimpleName() + ".addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {\n"
                                + "    @Override\n"
                                + "       public void onPropertyChanged($T observable, int i) {\n"
                                + "           $L.this.$L.set$L($L.get());\n"
                                + "       }\n"
                                + "   })";
                        setModelMethodBuilder.addStatement(setter, bindable, finalClassName, model, capitalize(fieldName), fieldName);
                    }
                }

            }

            databindingClassBuilder.addMethod(setModelMethodBuilder.build());
            databindingClassBuilder.addFields(fieldSpecList);

            //on enregistre la class
            JavaFile javaFile = JavaFile.builder(modelClassName.packageName(), databindingClassBuilder.build())
                    .build();
            javaFile.toJavaFileObject();
            try {
                javaFile.writeTo(processingEnvironment.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }*/


    }

    @Override
    public boolean useGeneratedAnnotation() {
        return false;
    }


}
