package com.leroymerlin.pandroid.plugin.test

import org.apache.commons.io.FileUtils
/**
 * Created by florian on 04/11/2016.
 */

public class TestUtils {

    static File pathToTests = new File("src/test/resources/android-app")
    static File pathToTmp = new File("build/testTmp")

    private TestUtils(){}



    public static final File initProjectDirectory(boolean withGradleProperties = true) {
        FileUtils.deleteDirectory(pathToTmp)
        FileUtils.copyDirectory(pathToTests, pathToTmp)
        if(!withGradleProperties){
            new File(pathToTmp, "build.gradle").delete()
        }
        return pathToTmp;
    }

    public static final void clearProjectDirectory(){
        FileUtils.deleteDirectory(pathToTmp)
    }
}
