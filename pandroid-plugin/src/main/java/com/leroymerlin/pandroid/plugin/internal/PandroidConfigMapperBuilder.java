package com.leroymerlin.pandroid.plugin.internal;

import com.leroymerlin.pandroid.annotations.PandroidGeneratedClass;
import com.leroymerlin.pandroid.app.PandroidMapper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

public class PandroidConfigMapperBuilder {

    public static final String FIELD_VIEW_SUPPORT = "VIEW_SUPPORT";


    List<String> libraries = new ArrayList<String>();
    List<FieldSpec> extraFields = new ArrayList<>();

    public PandroidConfigMapperBuilder() {
    }


    public void addExtraField(Class type, String name, String value){
        this.extraFields.add(FieldSpec.builder(type, name, Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC).initializer("$L", value).build());
    }

    public void addLibrary(String name) {
        this.libraries.add(name.toLowerCase());
    }

    static final List<String> fieldsName = Arrays.asList("DEBUG", "APPLICATION_ID", "BUILD_TYPE", "FLAVOR", "VERSION_CODE", "VERSION_NAME");
    static final List<String> mapperFieldsName = Arrays.asList("LIBRARIES", FIELD_VIEW_SUPPORT);


    public void buildClass(String applicationId, String packageName, File outputFolder) throws IOException {
        CodeBlock.Builder tagInitBlockBuilder = CodeBlock.builder().add("$T.asList(", Arrays.class);
        for (int i = 0; i < libraries.size(); i++) {
            if (i != 0)
                tagInitBlockBuilder.add(",");
            tagInitBlockBuilder.add("$S", libraries.get(i));
        }
        tagInitBlockBuilder.add(")");

        TypeSpec.Builder result =
                TypeSpec.classBuilder(PandroidMapper.MAPPER_IMPL_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(PandroidGeneratedClass.class)
                                .addMember("target", "$T.class", ClassName.VOID)
                                .addMember("type", "$T.class", ClassName.VOID)
                                .build())
                        .superclass(PandroidMapper.class)
                        .addField(FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "LIBRARIES")
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).initializer(tagInitBlockBuilder.build()).build());


        // ###### EXTRA FIELDS ######
        for(FieldSpec extraField : extraFields){
            result.addField(extraField);
        }
        // ###### EXTRA FIELDS ######


        // ###### PACKAGE ATTR ######
        result.addField(FieldSpec.builder(String.class, PandroidMapper.PACKAGE_ATTR, Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC).initializer("$S", packageName).build());
        // ###### PACKAGE ATTR ######

        // ###### setupConfig ######
        ClassName pandroidConfigClassName = ClassName.get("com.leroymerlin.pandroid.app", "PandroidConfig");
        ClassName buildConfigClassName = ClassName.get(packageName, "BuildConfig");
        MethodSpec.Builder setupConfigMethodBuilder = MethodSpec.methodBuilder("setupConfig").addAnnotation(Override.class).addModifiers(Modifier.PUBLIC);
        for (String field : fieldsName) {
            setupConfigMethodBuilder.addStatement("$T.$L = $T.$L", pandroidConfigClassName, field, buildConfigClassName, field);
        }
        for (String field : mapperFieldsName) {
            setupConfigMethodBuilder.addStatement("$T.$L = $T.$L", pandroidConfigClassName, field, PandroidMapper.MAPPER_IMPL_NAME, field);
        }
        result.addMethod(setupConfigMethodBuilder.build());
        // ###### setupConfig ######


        //###### GENERATED METHOD #######

        TypeVariableName t = TypeVariableName.get("T");
        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(List.class), t);
        ClassName wrapperClassName = ClassName.get(PandroidMapper.MAPPER_PACKAGE, PandroidMapper.WRAPPER_NAME);
        result.addMethod(
                MethodSpec.methodBuilder(PandroidMapper.WRAPPER_GENERATED_METHOD_NAME)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(t)
                        .returns(returnType)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), t), "type")
                        .addParameter(TypeName.OBJECT, "target")
                        .addStatement("return $T.$L(type, target)", wrapperClassName, PandroidMapper.WRAPPER_GENERATED_METHOD_NAME)
                        .build());
        //###### GENERATED METHOD #######


        //###### INJECT METHOD #######

        result.addMethod(
                MethodSpec.methodBuilder(PandroidMapper.WRAPPER_INJECT_METHOD_NAME)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.OBJECT, "component")
                        .addParameter(TypeName.OBJECT, "target")
                        .addStatement("$T.$L(component, target)", wrapperClassName, PandroidMapper.WRAPPER_INJECT_METHOD_NAME)
                        .build()
        );
        //###### INJECT METHOD #######


        JavaFile finalClass = JavaFile.builder(PandroidMapper.MAPPER_PACKAGE, result.build())
                .addFileComment("Generated code from Pandroid Plugin. Do not modify!")
                .build();

        finalClass.writeTo(outputFolder);
    }
}
