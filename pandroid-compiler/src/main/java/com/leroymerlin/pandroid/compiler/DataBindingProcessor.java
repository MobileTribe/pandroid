package com.leroymerlin.pandroid.compiler;

import com.google.common.collect.Lists;
import com.leroymerlin.pandroid.annotations.DataBinding;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by adrien le roy on 29/09/2016.
 */

public class DataBindingProcessor extends BaseProcessor {


    private final ClassName bindable = ClassName.get("android.databinding", "Observable");

    HashMap<ClassName, ClassName> observableClasses = new HashMap<ClassName, ClassName>() {{
        put(ClassName.get(String.class), ClassName.get("com.leroymerlin.pandroid.mvvm", "ObservableString"));
        put(ClassName.get(Short.class), ClassName.get("android.databinding", "ObservableShort"));
        put(ClassName.get(Integer.class), ClassName.get("com.leroymerlin.pandroid.mvvm", "PandroidObservableInt"));
        put(ClassName.get(Float.class), ClassName.get("android.databinding", "ObservableFloat"));
        put(ClassName.get(Long.class), ClassName.get("android.databinding", "ObservableLong"));
        put(ClassName.get(Double.class), ClassName.get("android.databinding", "ObservableDouble"));
        put(ClassName.get(Character.class), ClassName.get("android.databinding", "ObservableChar"));
        put(ClassName.get(Boolean.class), ClassName.get("android.databinding", "ObservableBoolean"));
        put(ClassName.get(Byte.class), ClassName.get("android.databinding", "ObservableByte"));
        put(ClassName.get("android.os", "Parcelable"), ClassName.get("android.databinding", "ObservableParcelable"));
        put(ClassName.get(Object.class), ClassName.get("android.databinding", "ObservableField"));
    }};

    public DataBindingProcessor(Elements elements, Types types) {
        super(elements, types);

    }

    @Override
    public List<String> getSupportedAnnotations() {
        return Lists.newArrayList(DataBinding.class.getCanonicalName());
    }


    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(DataBinding.class);
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            ClassName modelClassName = ClassName.get(typeElement);

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

            saveClass(processingEnvironment, modelClassName.packageName(),databindingClassBuilder);
        }


    }

    @Override
    public boolean useGeneratedAnnotation() {
        return false;
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

}