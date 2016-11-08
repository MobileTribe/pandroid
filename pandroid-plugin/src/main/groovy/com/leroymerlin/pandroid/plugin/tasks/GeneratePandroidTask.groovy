package com.leroymerlin.pandroid.plugin

import com.android.annotations.NonNull
import com.android.build.gradle.internal.core.GradleVariantConfiguration
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.tasks.BaseTask
import com.android.build.gradle.internal.variant.BaseVariantData
import com.android.build.gradle.internal.variant.BaseVariantOutputData
import com.leroymerlin.pandroid.plugin.internal.PandroidConfigMapperBuilder
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
public class GeneratePandroidTask extends BaseTask {

    // ----- PUBLIC TASK API -----

    private File sourceOutputDir;

    @OutputDirectory
    public File getSourceOutputDir() {
        return sourceOutputDir;
    }

    public void setSourceOutputDir(File sourceOutputDir) {
        this.sourceOutputDir = sourceOutputDir;
    }

    // ----- PRIVATE TASK API -----

    private PandroidConfigMapperBuilder mapperBuilder;

    private String applicationId;

    private String appPackageName;

    public void setMapperBuilder(PandroidConfigMapperBuilder mapperBuilder) {
        this.mapperBuilder = mapperBuilder;
    }

    @Input
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Input
    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    @TaskAction
    void generate() throws IOException {

        mapperBuilder.buildClass(this.applicationId, this.appPackageName, getSourceOutputDir())


    }

    // ----- Config Action -----

    public static final class ConfigAction {

        @NonNull
        private final VariantScope scope;
        private final PandroidConfigMapperBuilder mapperBuilder;

        public ConfigAction(
                @NonNull VariantScope scope, PandroidConfigMapperBuilder mapperBuilder) {
            this.scope = scope;
            this.mapperBuilder = mapperBuilder;
        }

        public String getName() {
            return scope.getTaskName("generate", "PandroidClasses");
        }

        public GeneratePandroidTask build(Project project) {
            BaseVariantData<? extends BaseVariantOutputData> variantData = scope.getVariantData();

            final GradleVariantConfiguration variantConfiguration =
                    variantData.getVariantConfiguration();

            GeneratePandroidTask task = project.task(getName(), type: GeneratePandroidTask)
            task.setApplicationId(variantData.applicationId)
            task.setVariantName(variantData.name)
            task.setAppPackageName(variantConfiguration.originalApplicationId)
            task.setMapperBuilder(this.mapperBuilder)
            task.setSourceOutputDir(new File(scope.globalScope.getBuildDir(), "generated" + "/source/pandroid/" + variantData.getVariantConfiguration().getDirName()));
            return task;
        }
    }
}