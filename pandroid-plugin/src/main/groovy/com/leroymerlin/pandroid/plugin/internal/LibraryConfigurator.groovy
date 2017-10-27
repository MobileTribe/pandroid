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


    public LibraryConfigurator(String name) {
        this.name = name;
    }

    def config = [:]

    void setProperty(String name, value) {
        config[name] = value
    }

    def methodMissing(String name, args) {
        return setProperty(name, args[0])
    }

    public void apply(Project project, Closure closure) throws ParserConfigurationException, SAXException, IOException {

        if (closure != null) {
            closure.setDelegate(this)
            closure.call()
        }
        if (manifest != null) {

            project.android.applicationVariants.all { variant ->
                variant.outputs.all { output ->
                    output.processManifest.doLast {
                        // Stores the path to the maifest.
                        [aaptFriendlyManifestOutputDirectory, instantRunManifestOutputDirectory, manifestOutputDirectory].each {
                            String manifestPath = "$it/AndroidManifest.xml"
                            // Stores the contents of the manifest.
                            def manifestFile = project.file(manifestPath)
                            if (manifestFile != null && manifestFile.exists()) {
                                XMLUtils.appendToXML(manifestFile, "<manifest>" + manifest.call(config) + "</manifest>")
                            }
                        }




                    }
                }
            }
        }

        if (gradle != null) {
            gradle.setDelegate(project);
            gradle.call(config);
        }

    }

    public void manifest(Closure manifest) {

        this.manifest = manifest;

    }

    public void gradle(Closure closure) {
        this.gradle = closure;
    }


}
