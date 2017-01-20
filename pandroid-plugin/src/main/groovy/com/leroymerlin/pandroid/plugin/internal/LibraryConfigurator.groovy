package com.leroymerlin.pandroid.plugin.internal

import com.leroymerlin.pandroid.plugin.utils.XMLUtils
import org.gradle.api.Project
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException

/**
 * Created by florian on 18/02/16.
 */
class LibraryConfigurator {
    String name;
    Closure manifest;
    Closure gradle;

    Object[] manifestConfig = new Object[0];

    public LibraryConfigurator(String name) {
        this.name = name;
    }

    public void manifestConfig(Object... objects){
        this.manifestConfig = objects;
    }

    public void apply(Project project, Closure closure) throws ParserConfigurationException, SAXException, IOException {

        if(closure!=null){
            closure.setDelegate(this)
            closure.run()
        }
        if (manifest != null) {

            project.android.applicationVariants.all {
                variant ->
                    variant.outputs.get(0).processManifest.doLast {
                        File[] manifestFiles = [
                            variant.outputs.get(0).processManifest.manifestOutputFile,
                            variant.outputs.get(0).processManifest.instantRunManifestOutputFile,
                            variant.outputs.get(0).processManifest.aaptFriendlyManifestOutputFile
                        ]
                        manifestFiles.each {
                            f ->
                                if(f!=null && f.exists()){
                                    XMLUtils.appendToXML(f, "<manifest>" + manifest.call(manifestConfig) + "</manifest>")
                                }
                        }
                    }
            }
        }

        if (gradle != null) {
            gradle.setDelegate(project);
            gradle.run();
        }

    }

    public void manifest(Closure manifest) {

        this.manifest = manifest;

    }

    public void gradle(Closure closure) {
        this.gradle = closure;
    }


}
