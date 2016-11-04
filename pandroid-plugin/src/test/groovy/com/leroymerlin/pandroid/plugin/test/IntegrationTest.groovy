package com.leroymerlin.pandroid.plugin.test

import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProgressEvent
import org.gradle.tooling.ProgressListener
import org.gradle.tooling.ProjectConnection
import org.junit.Before
import org.junit.Test
/**
 * Created by florian on 17/12/15.
 */
class IntegrationTest {

    @Before
    public void setUp() {
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(new File("pandroid-plugin"))
        ProjectConnection connection = connector.connect()
        try {
            BuildLauncher launcher = connection.newBuild()
            launcher.forTasks("install")
            launcher.run()
        } finally {
            connection.close()
        }
    }


    /**
     *
     * Testing Methods
     *
     */
    private static void testTask(String... tasks){
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(new File("pandroid-plugin/src/test/resources/android-app"))
        ProjectConnection connection = connector.connect()
        try {
            BuildLauncher launcher = connection.newBuild()
            launcher.forTasks(tasks)
            launcher.addProgressListener(new ProgressListener() {
                @Override
                void statusChanged(ProgressEvent progressEvent) {
                    println (progressEvent.description)
                }
            })
            launcher.run()
        } finally {
            connection.close()
        }
    }


    @Test
    public void testAssembleDebug() {
        testTask("clean", "assembleDebug")
    }



}
