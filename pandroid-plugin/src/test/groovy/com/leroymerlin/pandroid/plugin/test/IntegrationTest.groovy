package com.leroymerlin.pandroid.plugin.test

import org.gradle.tooling.*
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Created by florian on 17/12/15.
 */
class IntegrationTest {


    @Before
    public void setUp() {
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(new File(""))
        ProjectConnection connection = connector.connect()
        try {
            BuildLauncher launcher = connection.newBuild()
            launcher.forTasks(":install")
            launcher.run()
        } finally {
            connection.close()
        }

        TestUtils.initProjectDirectory()
    }

    @After
    public void tearDown() {
        TestUtils.clearProjectDirectory()
    }

    /**
     *
     * Testing Methods
     *
     */
    private static void testTask(String... tasks) {
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(TestUtils.pathToTmp)
        ProjectConnection connection = connector.connect()
        try {
            BuildLauncher launcher = connection.newBuild()
            launcher.forTasks(tasks).withArguments("--stacktrace")
            launcher.addProgressListener(new ProgressListener() {
                @Override
                void statusChanged(ProgressEvent progressEvent) {
                    println(progressEvent.description)
                }
            })
            launcher.setStandardOutput(System.out)
            launcher.setStandardError(System.out)
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
