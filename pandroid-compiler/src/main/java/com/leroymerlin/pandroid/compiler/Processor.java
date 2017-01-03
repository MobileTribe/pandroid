package com.leroymerlin.pandroid.compiler;


import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor {

    private ProcessingEnvironment mProcessingEnvironment;
    private Elements mElementsUtils;
    private ArrayList<BaseProcessor> processors;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mProcessingEnvironment = processingEnv;
        mElementsUtils = processingEnv.getElementUtils();


        processors = new ArrayList<BaseProcessor>();
        processors.add(new EventBusProcessor(mElementsUtils));
        processors.add(new LifecycleDelegateProcessor(mElementsUtils));
        processors.add(new DataBindingProcessor(mElementsUtils));
        processors.add(new GeneratedClassMapperProcessor(mElementsUtils));

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
        boolean generatedSources = false;
        for (BaseProcessor processor : processors) {
            processor.setClassGenerated(generatedSources);
            processor.process(roundEnv, mProcessingEnvironment);
            if (processor.useGeneratedAnnotation())
                generatedSources = processor.isClassGenerated();
        }
        return false;
    }
}
