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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import dagger.Component;

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
        return Lists.newArrayList(
                PandroidGeneratedClass.class.getCanonicalName(),
                Component.class.getCanonicalName()
        );
    }

    Map<DeclaredType, Map<DeclaredType, TypeElement>> dataMap = new HashMap<>();
    Map<TypeMirror, List<TypeName>> injectList = new HashMap<>();

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

        TypeElement baseComponentType = processingEnvironment.getElementUtils().getTypeElement("com.leroymerlin.pandroid.dagger.BaseComponent");


        Set<? extends Element> daggerComponentElements = roundEnvironment.getElementsAnnotatedWith(Component.class);
        for (Element element : daggerComponentElements) {
            TypeElement typeElement = (TypeElement) element;

            if (typeElement.getInterfaces().contains(baseComponentType.asType())) {
                extractInjectMethod(baseComponentType);
            }
            extractInjectMethod(typeElement);
        }


        if (!fileSaved && !isClassGenerated() && !roundEnvironment.processingOver()) {


            TypeElement pandroidMapperImplType = processingEnvironment.getElementUtils().getTypeElement(PandroidMapper.MAPPER_PACKAGE + "." + PandroidMapper.MAPPER_IMPL_NAME);

            boolean library = false;
            if (pandroidMapperImplType == null) {
                library = true;
            }

            /*String packageName = null;
            for (Element element : pandroidMapperImplType.getEnclosedElements()) {
                if (PandroidMapper.PACKAGE_ATTR.equals(element.getSimpleName().toString())) {
                    packageName = (String) ((VariableElement) element).getConstantValue();
                    break;
                }
            }

            if (packageName == null) {
                log(processingEnvironment, "Can't find packageName", Diagnostic.Kind.ERROR);
            }*/

            String wrapperName = null;
            TypeElement lastWrapper = null;
            int i = 0;
            do {
                wrapperName = PandroidMapper.WRAPPER_NAME + "$_" + i++;

                TypeElement libWrapper = processingEnvironment.getElementUtils().getTypeElement(PandroidMapper.MAPPER_PACKAGE + "." + wrapperName);
                if (libWrapper == null) {
                    break;
                }
                lastWrapper = libWrapper;
            }
            while (true);


            if (!library) {
                wrapperName = PandroidMapper.WRAPPER_NAME;
            }


            TypeSpec.Builder wrapperBuilder = TypeSpec.classBuilder(wrapperName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


            // ##### GENERATE INSTANCE METHOD #####

            TypeVariableName t = TypeVariableName.get("T");
            ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(List.class), t);
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(PandroidMapper.WRAPPER_GENERATED_METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addTypeVariable(t)
                    .returns(returnType)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), t), "type")
                    .addParameter(TypeName.OBJECT, "target")

                    .addStatement("$T result = new $T()", returnType, ParameterizedTypeName.get(ClassName.get(ArrayList.class), t));
            if (lastWrapper != null) {
                methodBuilder.addStatement("result.addAll($T.$L(type, target))", lastWrapper, PandroidMapper.WRAPPER_GENERATED_METHOD_NAME);
            }

            CodeBlock.Builder codeBuilder = CodeBlock.builder();
            for (Map.Entry<DeclaredType, Map<DeclaredType, TypeElement>> entry : dataMap.entrySet()) {
                codeBuilder.beginControlFlow("if(type.equals($T.class))", entry.getKey());
                for (Map.Entry<DeclaredType, TypeElement> content : entry.getValue().entrySet()) {
                    codeBuilder.beginControlFlow("if($T.class.isAssignableFrom(target.getClass()))", content.getKey());
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

            // ##### INJECT METHOD #####
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(PandroidMapper.WRAPPER_INJECT_METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addParameter(TypeName.OBJECT, "component")
                    .addParameter(TypeName.OBJECT, "target");

            if (lastWrapper != null) {
                injectMethodBuilder.addStatement("$T.$L(component, target)", lastWrapper, PandroidMapper.WRAPPER_INJECT_METHOD_NAME);
            }
            CodeBlock.Builder code = CodeBlock.builder();

            for (Map.Entry<TypeMirror, List<TypeName>> set : injectList.entrySet()) {
                code.beginControlFlow("if(component instanceof $T)", set.getKey());

                for (TypeName typeName : set.getValue()) {
                    code
                            .beginControlFlow("if(target instanceof $T)", typeName)
                            .addStatement("(($T)component).inject(($T)target)", set.getKey(), typeName)
                            .endControlFlow();

                }
                code.endControlFlow();

            }

            injectMethodBuilder.addCode(code.build());
            wrapperBuilder.addMethod(injectMethodBuilder.build());

            saveClass(processingEnvironment, PandroidMapper.MAPPER_PACKAGE, wrapperBuilder);
            fileSaved = true;
        }


    }

    private void extractInjectMethod(TypeElement typeElement) {
        List<TypeName> types = new ArrayList<>();
        for (Element content : typeElement.getEnclosedElements()) {
            if (content.getKind().equals(ElementKind.METHOD) && content.getSimpleName().toString().startsWith("inject")) {
                ExecutableElement method = (ExecutableElement) content;
                if (method.getParameters().size() == 1) {
                    TypeMirror paramType = method.getParameters().get(0).asType();
                    TypeName e = ClassName.get(paramType);
                    if (e instanceof ParameterizedTypeName) {
                        e = ((ParameterizedTypeName) e).rawType;
                    }
                    types.add(e);
                }
            }
        }
        if (!types.isEmpty()) {
            injectList.put(typeElement.asType(), types);
        }

    }

    @Override
    public boolean useGeneratedAnnotation() {
        return false;
    }


}
