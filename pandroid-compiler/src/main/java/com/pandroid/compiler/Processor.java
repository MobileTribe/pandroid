package com.pandroid.compiler;


import com.google.auto.service.AutoService;
import com.pandroid.annotations.DataBinding;
import com.pandroid.annotations.EventReceiver;

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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mProcessingEnvironment = processingEnv;
        mElementsUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedSet = new HashSet<>();
        supportedSet.add(EventReceiver.class.getCanonicalName());
        supportedSet.add(DataBinding.class.getCanonicalName());
        return supportedSet;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        new EventBusProcessor(mElementsUtils).process(roundEnv, mProcessingEnvironment);
        new DataBindingProcessor(mElementsUtils).process(roundEnv, mProcessingEnvironment);
        return false;
    }
}
