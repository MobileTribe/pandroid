package com.leroymerlin.pandroid.compiler;


import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor {

    private ProcessingEnvironment mProcessingEnvironment;
    private Elements mElementsUtils;
    private Types mTypesUtils;

    private ArrayList<BaseProcessor> processors;
    private double duration;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mProcessingEnvironment = processingEnv;
        mElementsUtils = processingEnv.getElementUtils();
        mTypesUtils = processingEnv.getTypeUtils();


        processors = new ArrayList<BaseProcessor>();
        processors.add(new EventBusProcessor(mElementsUtils, mTypesUtils));
        processors.add(new LifecycleDelegateProcessor(mElementsUtils, mTypesUtils));
        processors.add(new DataBindingProcessor(mElementsUtils, mTypesUtils));
        processors.add(new GeneratedClassMapperProcessor(mElementsUtils, mTypesUtils));

        duration = 0;

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedSet = new HashSet<>();
        for (BaseProcessor processor : processors) {
            supportedSet.addAll(processor.getSupportedAnnotations());
        }
        return supportedSet;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        double startTime = System.currentTimeMillis();

        boolean generatedSources = false;
        for (BaseProcessor processor : processors) {
            processor.setClassGenerated(generatedSources);
            processor.process(roundEnv, mProcessingEnvironment);
            if (processor.useGeneratedAnnotation())
                generatedSources = processor.isClassGenerated();
        }

        duration += (System.currentTimeMillis() - startTime);
        if (roundEnv.processingOver()) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(Locale.getDefault(), "Pandroid processor took %s ms", duration));
        }
        return false;
    }
}
