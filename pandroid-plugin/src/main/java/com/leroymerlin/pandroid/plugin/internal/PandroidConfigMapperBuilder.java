package com.leroymerlin.pandroid.plugin.internal;

import com.leroymerlin.pandroid.app.ConfigMapperConst;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

public class PandroidConfigMapperBuilder {


    List<String> libraries = new ArrayList<String>();

    public PandroidConfigMapperBuilder() {
    }

    public void addLibrary(String name) {
        this.libraries.add(name);
    }

    static final List<String> fieldsName = Arrays.asList("DEBUG", "APPLICATION_ID", "BUILD_TYPE", "FLAVOR", "VERSION_CODE", "VERSION_NAME");


    public void buildClass(String applicationId, String packageName, File outputFolder) throws IOException {
        CodeBlock.Builder tagInitBlockBuilder = CodeBlock.builder().add("$T.asList(", Arrays.class);
        for (int i = 0; i < libraries.size(); i++) {
            if (i != 0)
                tagInitBlockBuilder.add(",");
            tagInitBlockBuilder.add("$S", libraries.get(i));
        }
        tagInitBlockBuilder.add(")");

        TypeSpec.Builder result =
                TypeSpec.classBuilder(ConfigMapperConst.MAPPER_NAME).addModifiers(Modifier.PUBLIC).addModifiers(Modifier.FINAL)
                        .addField(FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "LIBRARIES")
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).initializer(tagInitBlockBuilder.build()).build());

        ClassName pandroidConfigClassName = ClassName.get("com.leroymerlin.pandroid.app", "PandroidConfig");
        ClassName buildConfigClassName = ClassName.get(packageName, "BuildConfig");
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("setupConfig").addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        for (String field : fieldsName) {
            methodBuilder.addStatement("$T.$L = $T.$L", pandroidConfigClassName, field, buildConfigClassName, field);
        }

        methodBuilder.addStatement("$T.LIBRARIES = $L.LIBRARIES", pandroidConfigClassName, ConfigMapperConst.MAPPER_NAME);
        result.addMethod(methodBuilder.build());
        JavaFile finalClass = JavaFile.builder(applicationId, result.build())
                .addFileComment("Generated code from Pandroid Plugin. Do not modify!")
                .build();

        finalClass.writeTo(outputFolder);
    }
}
