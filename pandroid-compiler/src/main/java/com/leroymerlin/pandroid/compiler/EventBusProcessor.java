package com.leroymerlin.pandroid.compiler;

import com.google.common.collect.Lists;
import com.leroymerlin.pandroid.annotations.EventReceiver;
import com.leroymerlin.pandroid.annotations.PandroidGeneratedClass;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by florian on 30/11/15.
 */
public class EventBusProcessor extends BaseProcessor {


    public EventBusProcessor(Elements elements, Types types) {
        super(elements, types);
    }

    @Override
    public List<String> getSupportedAnnotations() {
        return Lists.newArrayList(EventReceiver.class.getCanonicalName());
    }

    @Override
    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
        try {
            Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(EventReceiver.class);
            Map<ClassName, List<ExecutableElement>> infosMap = new HashMap<>();
            for (Element element : elementsAnnotatedWith) {
                ExecutableElement e = (ExecutableElement) element;
                TypeElement enclosingElement = (TypeElement) e.getEnclosingElement();
                ClassName parentClassName = ClassName.get(enclosingElement);

                List<ExecutableElement> methodElements = infosMap.get(parentClassName);
                if (methodElements == null) {
                    methodElements = new ArrayList<>();
                }
                methodElements.add(e);
                infosMap.put(parentClassName, methodElements);
            }

            for (ClassName parentClassName : infosMap.keySet()) {
                Method ReceiverProvider_MethodReceivers = ReceiversProvider.class.getMethod("getReceivers");

                Method EventBusReceiver_MethodTags = EventBusManager.EventBusReceiver.class.getMethod("getTags");
                Method EventBusReceiver_MethodHandle = EventBusManager.EventBusReceiver.class.getMethod("handle", Object.class);


                ParameterizedTypeName weakReferenceType = ParameterizedTypeName.get(ClassName.get(WeakReference.class), parentClassName);


                ParameterizedTypeName listReceiverType = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(EventBusManager.EventBusReceiver.class));
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(parentClassName, "reference")
                        .addStatement("weakReference = new $T(reference)", weakReferenceType)
                        .addCode("receivers = new $T(){{\n", listReceiverType);
                for (ExecutableElement e : infosMap.get(parentClassName)) {

                    String[] receiverTags = e.getAnnotation(EventReceiver.class)
                            .value();

                    CodeBlock.Builder tagInitBlockBuilder = CodeBlock.builder().add("$T.asList(", Arrays.class);

                    for (int i = 0; i < receiverTags.length; i++) {
                        if (i != 0)
                            tagInitBlockBuilder.add(",");
                        tagInitBlockBuilder.add("$S", receiverTags[i]);
                    }
                    tagInitBlockBuilder.add(")");
                    List<? extends VariableElement> params = e.getParameters();
                    if (params.size() > 1) {
                        log(processingEnvironment, getFullName(e) + " should have 1 or no parameter, have " + params.size(), Diagnostic.Kind.ERROR);
                    }

                    TypeName parameterTypeName = null;

                    if (params.size() == 1) {
                        parameterTypeName = ClassName.get(params.get(0).asType());
                        if (parameterTypeName instanceof ParameterizedTypeName) { // remove <T> from typeName for instanceOf
                            parameterTypeName = ((ParameterizedTypeName) parameterTypeName).rawType;
                        }
                    }

                    MethodSpec.Builder receiverMethodBuilder = MethodSpec.methodBuilder(EventBusReceiver_MethodHandle.getName()).addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(TypeName.BOOLEAN)
                            .addParameter(EventBusReceiver_MethodHandle.getParameterTypes()[0], "data");


                    if (parameterTypeName != null) {
                        receiverMethodBuilder.beginControlFlow("if(weakReference.get() != null && ( ( data instanceof $T ) || ( data == null && !getTags().isEmpty() ) ) )", parameterTypeName.box())
                                .addStatement("weakReference.get()." + getMethodName(e) + "(($T) data)", parameterTypeName);
                    } else {
                        receiverMethodBuilder.beginControlFlow("if(weakReference.get() != null)")
                                .addStatement("weakReference.get()." + getMethodName(e) + "()");
                    }
                    receiverMethodBuilder.addStatement("return true")
                            .endControlFlow()
                            .addStatement("return false");

                    MethodSpec receiverMethod = receiverMethodBuilder.build();

                    TypeSpec eventBusReceiverClass = TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(EventBusManager.EventBusReceiver.class)
                            .addField(FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "tags").addModifiers(Modifier.PRIVATE, Modifier.FINAL).initializer(tagInitBlockBuilder.build()).build())
                            .addMethod(
                                    MethodSpec.methodBuilder(EventBusReceiver_MethodTags.getName()).addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(EventBusReceiver_MethodTags.getReturnType())
                                            .addStatement("return tags").build())

                            .addMethod(receiverMethod)
                            .build();

                    constructorBuilder.addStatement("add($L)", eventBusReceiverClass);

                }
                constructorBuilder.addStatement("}}");


                TypeSpec.Builder receiverClassBuilder = TypeSpec.classBuilder(parentClassName.simpleName() + ReceiversProvider.SUFFIX_RECEIVER_PROVIDER)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ReceiversProvider.class)
                        .addField(weakReferenceType, "weakReference", Modifier.PRIVATE)
                        .addField(listReceiverType, "receivers", Modifier.PRIVATE)
                        .addAnnotation(AnnotationSpec.builder(PandroidGeneratedClass.class)
                                .addMember("target", "$T.class", parentClassName)
                                .addMember("type", "$T.class", ReceiversProvider.class)
                                .build())
                        .addMethod(constructorBuilder.build());


                MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder(ReceiverProvider_MethodReceivers.getName())
                        .returns(ReceiverProvider_MethodReceivers.getReturnType()).addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
                        .addStatement("return receivers");
                receiverClassBuilder.addMethod(getMethodBuilder.build());


                saveClass(processingEnvironment, parentClassName.packageName(), receiverClassBuilder);

            }


        } catch (Exception e) {
            log(processingEnvironment, e.getMessage(), Diagnostic.Kind.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public boolean useGeneratedAnnotation() {
        return true;
    }


}
