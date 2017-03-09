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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by florian on 30/11/15.
 */
public class GeneratedClassMapperProcessor extends BaseProcessor {


    private static final String TARGET_VAR_NAME = "target";
    private static final String TYPE_VAR_NAME = "type";
    private static final String COMPONENT_VAR_NAME = "component";
    private static final String INSTANCEOF_BLOCK_CONDITION = "$L instanceof $T";
    private static final String CLASS_EQUALS_BLOCK_CONDITION = "$L.equals($T.class)";
    private boolean fileSaved;

    public GeneratedClassMapperProcessor(Elements elements, Types types) {
        super(elements, types);
    }


    @Override
    public List<String> getSupportedAnnotations() {
        return Lists.newArrayList(
                PandroidGeneratedClass.class.getCanonicalName(),
                Component.class.getCanonicalName(),
                Subcomponent.class.getCanonicalName()
        );
    }

    //Map<DeclaredType, Map<DeclaredType, TypeElement>> dataMap = new HashMap<>();
    private BlockList<GenerateBlock> generatedBlocks = new BlockList<GenerateBlock>(CLASS_EQUALS_BLOCK_CONDITION) {
        private static final long serialVersionUID = 22771856657051246L;

        // Generated block can't have child is they check class equality
        @Override
        public boolean add(GenerateBlock block) {
            super.add(0, block);
            return true;
        }
    };
    private BlockList<ComponentBlock> componentBlocks = new BlockList<>(INSTANCEOF_BLOCK_CONDITION);

    @Override
    public void process(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(PandroidGeneratedClass.class);
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            ClassName modelClassName = ClassName.get(typeElement);
            if (modelClassName.equals(ClassName.get(PandroidMapper.MAPPER_PACKAGE, PandroidMapper.MAPPER_IMPL_NAME))) {
                continue; //ignore PandroidMappleImpl class
            }

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

                GenerateBlock generatedBlock = new GenerateBlock(type);
                int index = generatedBlocks.indexOf(generatedBlock);
                if (index >= 0) {
                    generatedBlock = generatedBlocks.get(index);
                } else {
                    generatedBlocks.add(generatedBlock);
                }
                generatedBlock.create.add(new CreateBlock(target, typeElement));
            }
        }

        TypeElement baseComponentType = processingEnvironment.getElementUtils().getTypeElement("com.leroymerlin.pandroid.dagger.BaseComponent");

        ArrayList<Element> daggerComponentElements = new ArrayList<>();
        daggerComponentElements.addAll(roundEnvironment.getElementsAnnotatedWith(Subcomponent.class));
        daggerComponentElements.addAll(roundEnvironment.getElementsAnnotatedWith(Component.class));
        for (Element element : daggerComponentElements) {
            TypeElement typeElement = (TypeElement) element;

            if (typeElement.getInterfaces().contains(baseComponentType.asType())) {
                extractInjectMethod(baseComponentType);
            }
            extractInjectMethod(typeElement);
        }


        //   RUN ONLY ON LAST ROUND (no generated classes detected)
        if (!fileSaved && !isClassGenerated() && !roundEnvironment.processingOver()) {


            TypeElement pandroidMapperImplType = processingEnvironment.getElementUtils().getTypeElement(PandroidMapper.MAPPER_PACKAGE + "." + PandroidMapper.MAPPER_IMPL_NAME);

            boolean library = false;
            if (pandroidMapperImplType == null) {
                library = true;
            }

            String wrapperName;
            TypeElement lastWrapper = null;
            int classIndex = 0;
            do {
                wrapperName = PandroidMapper.WRAPPER_NAME + "$_" + classIndex++;

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
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), t), TYPE_VAR_NAME)
                    .addParameter(TypeName.OBJECT, TARGET_VAR_NAME)

                    .addStatement("$T result = new $T()", returnType, ParameterizedTypeName.get(ClassName.get(ArrayList.class), t));
            if (lastWrapper != null) {
                methodBuilder.addStatement("result.addAll($T.$L(type, target))", lastWrapper, PandroidMapper.WRAPPER_GENERATED_METHOD_NAME);
            }

            methodBuilder.addCode(generatedBlocks.toCodeBlock(TYPE_VAR_NAME, null));

            methodBuilder.addStatement("return result");
            wrapperBuilder.addMethod(methodBuilder.build());

            // ##### INJECT METHOD #####
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(PandroidMapper.WRAPPER_INJECT_METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addParameter(TypeName.OBJECT, COMPONENT_VAR_NAME)
                    .addParameter(TypeName.OBJECT, TARGET_VAR_NAME);

            if (lastWrapper != null) {
                injectMethodBuilder.addStatement("$T.$L(component, $L)", lastWrapper, PandroidMapper.WRAPPER_INJECT_METHOD_NAME, TARGET_VAR_NAME);
            }

            CodeBlock codeBlock = componentBlocks.toCodeBlock(COMPONENT_VAR_NAME, null);
            injectMethodBuilder.addCode(codeBlock);
            wrapperBuilder.addMethod(injectMethodBuilder.build());

            saveClass(processingEnvironment, PandroidMapper.MAPPER_PACKAGE, wrapperBuilder);
            fileSaved = true;
        }


    }

    private void extractInjectMethod(TypeElement typeElement) {

        ComponentBlock currentBlock = new ComponentBlock(typeElement.asType());
        for (Element content : typeElement.getEnclosedElements()) {
            if (content.getKind().equals(ElementKind.METHOD) && content.getSimpleName().toString().startsWith("inject")) {
                ExecutableElement method = (ExecutableElement) content;
                if (method.getParameters().size() == 1) {
                    VariableElement variableElement = method.getParameters().get(0);
                    TypeMirror paramType = mTypesUtils.erasure(variableElement.asType());
                    currentBlock.injects.add(new InjectBlock(paramType));
                }
            }
        }

        if (!currentBlock.injects.isEmpty())
            componentBlocks.add(currentBlock);

    }

    @Override
    public boolean useGeneratedAnnotation() {
        return false;
    }


    abstract class Block implements Comparable<Block> {
        final TypeMirror type;

        BlockList<Block> childs;

        Block(TypeMirror type, String condition) {
            this.type = type;
            childs = new BlockList<>(condition);
        }

        TypeName getTypeName() {
            TypeName e = ClassName.get(type);
            if (e instanceof ParameterizedTypeName) {
                e = ((ParameterizedTypeName) e).rawType;
            }
            return e;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof TypeMirror && o.equals(type) || o instanceof Block && ((Block) o).type.equals(type);

        }


        @Override
        public int compareTo(@Nonnull Block block) {
            if (mTypesUtils.isAssignable(block.type, type)) {
                return 1;
            } else if (mTypesUtils.isAssignable(type, block.type)) {
                return -1;
            } else {
                return 0;
            }
        }

        public abstract CodeBlock toCodeBlock(TypeName parentType);
    }

    // GENERATE BLOCKS

    private class GenerateBlock extends Block {
        BlockList<CreateBlock> create = new BlockList<>(INSTANCEOF_BLOCK_CONDITION);

        GenerateBlock(TypeMirror type) {
            super(type, CLASS_EQUALS_BLOCK_CONDITION);
        }

        @Override
        public CodeBlock toCodeBlock(TypeName parentType) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.add(childs.toCodeBlock(TYPE_VAR_NAME, getTypeName()));
            code.add(create.toCodeBlock(TARGET_VAR_NAME, getTypeName()));
            return code.build();
        }
    }

    private class CreateBlock extends Block {

        private final TypeElement typeElement;

        CreateBlock(TypeMirror type, TypeElement typeElement) {
            super(type, INSTANCEOF_BLOCK_CONDITION);
            this.typeElement = typeElement;
        }

        @Override
        public CodeBlock toCodeBlock(TypeName parentType) {
            CodeBlock.Builder code = CodeBlock.builder();

            for (Element enclosed : typeElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                    ExecutableElement constructorElement = (ExecutableElement) enclosed;
                    if (constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                        if (constructorElement.getParameters().size() == 0) {
                            code.addStatement("result.add((T)new $T())", typeElement);
                            break;
                        } else if (constructorElement.getParameters().size() == 1) {
                            code.addStatement("result.add((T)new $T(($T)$L))", typeElement, type, TARGET_VAR_NAME);
                            break;
                        }
                    }
                }
            }
            return code.build();
        }
    }

    // INJECT BLOCKS
    private class InjectBlock extends Block {

        InjectBlock(TypeMirror type) {
            super(type, INSTANCEOF_BLOCK_CONDITION);
        }

        @Override
        public CodeBlock toCodeBlock(TypeName parentType) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.addStatement("(($T)$L).inject(($T)$L)", parentType, COMPONENT_VAR_NAME, this.getTypeName(), TARGET_VAR_NAME);
            code.add(childs.toCodeBlock(TARGET_VAR_NAME, parentType));
            return code.build();
        }
    }

    private class ComponentBlock extends Block {
        BlockList<InjectBlock> injects = new BlockList<>(INSTANCEOF_BLOCK_CONDITION);

        ComponentBlock(TypeMirror type) {
            super(type, INSTANCEOF_BLOCK_CONDITION);
        }

        @Override
        public CodeBlock toCodeBlock(TypeName parentType) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.add(childs.toCodeBlock(COMPONENT_VAR_NAME, parentType));
            code.add(injects.toCodeBlock(TARGET_VAR_NAME, parentType));
            return code.build();
        }
    }


    private class BlockList<T extends Block> extends ArrayList<T> {

        private static final long serialVersionUID = -951463758540572154L;
        final String condition;

        BlockList(String condition) {
            this.condition = condition;
        }

        @Override
        public boolean add(T block) {
            int i = 0;
            while (i < size()) {
                Block blockIndex = get(i);
                int result = blockIndex.compareTo(block);
                if (result > 0) {
                    blockIndex.childs.add(block);
                    return true;
                } else if (result < 0) {
                    remove(i);
                    block.childs.add(blockIndex);
                } else {
                    i++;
                }
            }
            return super.add(block);
        }

        CodeBlock toCodeBlock(String varName, TypeName parentComponent) {
            CodeBlock.Builder code = CodeBlock.builder();

            for (int i = 0; i < size(); i++) {
                T componentBlock = get(i);
                if (i == 0) {
                    code.beginControlFlow("if(" + condition + ")", varName, componentBlock.getTypeName());
                } else {
                    code.nextControlFlow("else if(" + condition + ")", varName, componentBlock.getTypeName());
                }
                TypeName parentType = parentComponent;
                if (componentBlock instanceof ComponentBlock) {
                    parentType = componentBlock.getTypeName();
                }
                code.add(componentBlock.toCodeBlock(parentType));
            }

            if (!isEmpty()) {
                code.endControlFlow();
            }
            return code.build();
        }
    }
}
