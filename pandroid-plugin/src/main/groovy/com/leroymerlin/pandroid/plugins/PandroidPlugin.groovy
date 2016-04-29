package com.leroymerlin.pandroid.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PandroidPlugin implements Plugin<Project> {

    static final String PANDROID_GROUP = "pandroid"
    Logger logger = LoggerFactory.getLogger('PandroidPlugin')

    def gradleFiles = [
            'pandroid-version.properties',
            'pandroid.properties',
            'dagger.gradle',
            'pandroid.gradle',
            'pandroid-proguard-rules.pro'
    ]

    Project project;

    def void apply(Project project) {
        this.project = project;

        project.extensions.create('pandroid', PandroidPluginExtension, project, this)

        File pandroid = project.file('pandroid.properties')
        applyPropertiesOnProject(pandroid)


        project.task("copyEmbededFiles", group: PANDROID_GROUP) << this.&copyEmbededFiles
        project.preBuild.dependsOn project.copyEmbededFiles

        applyEmbededFiles()

        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
//                compileDeps.add(project.getDependencies().create("org.foo:bar:$version"))
                project.getGradle().removeListener(this)

            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {}
        })


    }


    File getEmbededFile(String fileName) {
        File file = new File(project.buildDir, "pandroid/$fileName");
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
        if(!file.exists())
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


