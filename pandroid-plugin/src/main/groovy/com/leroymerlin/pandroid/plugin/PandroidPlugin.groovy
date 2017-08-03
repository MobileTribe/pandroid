package com.leroymerlin.pandroid.plugin

import com.leroymerlin.pandroid.plugin.internal.PandroidConfigMapperBuilder
import com.leroymerlin.pandroid.plugin.GeneratePandroidTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PandroidPlugin implements Plugin<Project> {

    static final String PANDROID_GROUP = "pandroid"
    static final String PROP_FILE = 'pandroid-version.properties'
    Logger logger = LoggerFactory.getLogger('PandroidPlugin')

    def pluginVersion
    PandroidConfigMapperBuilder configMapperBuilder;

    def gradleFiles = [
            PROP_FILE,
            'pandroid.properties',
            'dagger.gradle',
            'pandroid.gradle',
            'pandroid-proguard-rules.pro'
    ]

    Project project;

    def void apply(Project project) {
        this.project = project;
        this.configMapperBuilder = new PandroidConfigMapperBuilder();

        if (!project.file(getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()).isDirectory()) {
            Properties properties = new Properties()
            properties.load(project.zipTree(getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()).matching {
                include PROP_FILE
            }.singleFile.newInputStream());
            pluginVersion = properties.getProperty("pandroidVersion")
        }

        project.extensions.create('pandroid', PandroidPluginExtension, project, this)

        File pandroid = project.file('pandroid.properties')
        applyPropertiesOnProject(pandroid)


        project.task("copyEmbededFiles", group: PANDROID_GROUP) << this.&copyEmbededFiles
        project.preBuild.dependsOn project.copyEmbededFiles

        applyEmbededFiles()

        //Support for kotlin projects
        project.plugins.withId('kotlin-android') {
            def kotlinKaptPluginId = 'kotlin-kapt'
            if (!project.plugins.hasPlugin(kotlinKaptPluginId)) {
                project.apply plugin: kotlinKaptPluginId
            }
            project.configurations.annotationProcessor.getAllDependencies().all {
                project.dependencies {
                    kapt it
                }
            }
        }


        def isAndroidApp = project.plugins.hasPlugin("com.android.application")
        if (isAndroidApp) {
            project.android.applicationVariants.all { variant ->
                def task = new GeneratePandroidTask.ConfigAction(variant.variantData.scope, configMapperBuilder).build(project);
                variant.registerJavaGeneratingTask(task, task.getSourceOutputDir())
            }
        }
    }

    File getEmbededFile(String fileName) {
        File pandroidFolder = new File(project.buildDir, "pandroid");
        if (pluginVersion) {
            File lockFile = new File(pandroidFolder, ".pandroid-" + pluginVersion);
            if (!lockFile.exists() && pandroidFolder.exists()) {
                logger.info("new version detected => delete old pandroid folder")
                pandroidFolder.deleteDir()
            }
            pandroidFolder.mkdirs();
            lockFile.createNewFile();
        }
        File file = new File(pandroidFolder, fileName);
        logger.info("file: $file")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            def pandroidTxt = project.zipTree(getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm()).matching {
                include fileName
            }.singleFile.text
            file.text = pandroidTxt;
            logger.info("$file created")
        }
        return file;
    }

    public void applyPropertiesOnProject(File file) {
        if (!file.exists())
            return
        Properties properties = new Properties();
        properties.load(new FileInputStream(file))
        properties.each { prop ->
            setProjectProperty(prop.key, prop.value)
        }
    }

    public void setProjectProperty(def key, def value) {
        project.ext.set(key, value);
        if (key == 'version')
            project.setProperty(key, value);
    }

    public void copyEmbededFiles() {
        gradleFiles.each {
            fileName ->
                getEmbededFile(fileName);
        }
    }

    public void applyEmbededFiles() {
        gradleFiles.each {
            fileName ->
                def file = getEmbededFile(fileName);
                if (fileName.endsWith(".properties")) {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(file))
                    properties.each { prop ->
                        project.ext.set(prop.key, prop.value)
                    }
                } else if (fileName.endsWith(".gradle")) {
                    project.apply from: file;
                }
        }
    }

}


