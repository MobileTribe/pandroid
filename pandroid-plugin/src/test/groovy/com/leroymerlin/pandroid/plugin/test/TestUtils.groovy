package com.leroymerlin.pandroid.plugin.test

import org.apache.commons.io.FileUtils

/**
 * Created by florian on 04/11/2016.
 */

public class TestUtils {

    static File pathToTests = new File(getPluginBaseDir(), "src/test/resources/android-app")
    static File pathToTmp = new File(getPluginBaseDir(), "build/testTmp")

    private TestUtils() {}


    static File getPluginBaseDir() {
        def file = new File("pandroid-plugin")
        if (file.exists()) {
            return file
        }
        return new File("").absoluteFile
    }

    public static final File initProjectDirectory(boolean withGradleProperties = true) {
        FileUtils.deleteDirectory(pathToTmp)
        FileUtils.copyDirectory(pathToTests, pathToTmp)
        if (!withGradleProperties) {
            new File(pathToTmp, "build.gradle").delete()
        }
        return pathToTmp;
    }

    public static final void clearProjectDirectory() {
        FileUtils.deleteDirectory(pathToTmp)
    }
}
