package com.leroymerlin.pandroid.plugin

import com.leroymerlin.pandroid.plugin.internal.LibraryConfigurator
import com.leroymerlin.pandroid.plugin.internal.SecureProperty
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

public class PandroidPluginExtension {

    def plugin;
    def project;

    final NamedDomainObjectContainer<LibraryConfigurator> configurators
    final NamedDomainObjectContainer<SecureProperty> secureProperties

    boolean enableViewSupport = false


    PandroidPluginExtension(Project project, PandroidPlugin plugin) {
        this.plugin = plugin;
        this.project = project;
        configurators = project.container(LibraryConfigurator)
        secureProperties = project.container(SecureProperty)


    }

    void configurators(Action<? super NamedDomainObjectContainer<LibraryConfigurator>> action) {
        action.execute(configurators)
    }

    void library(String regex, Closure closure = null) {
        configurators.each {
            pandroidDep ->
                if ((pandroidDep.name =~ ".*" + regex + ".*").matches()) {
                    plugin.logger.info("$regex => provide ${pandroidDep.name}")
                    plugin.configMapperBuilder.addLibrary(pandroidDep.name)
                    pandroidDep.apply(project, closure)
                }
        }
    }

    void secureConfigFields(Action<? super NamedDomainObjectCollection<SecureProperty>> action){
        action.execute(secureProperties)
    }


}