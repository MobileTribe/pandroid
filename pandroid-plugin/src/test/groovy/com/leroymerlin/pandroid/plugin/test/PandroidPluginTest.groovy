package com.leroymerlin.pandroid.plugin.test

import com.leroymerlin.pandroid.plugin.PandroidPlugin
import com.leroymerlin.pandroid.plugin.PandroidPluginExtension
import com.leroymerlin.pandroid.plugin.utils.XMLUtils
import com.leroymerlin.pandroid.security.AESEncryption
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by florian on 17/12/15.
 */
class PandroidPluginTest {

    Project project
    File projectDir;

    @Before
    public void setUp() {
        projectDir = TestUtils.initProjectDirectory(false);
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        def buildDir = project.file("build")
        project.buildDir = buildDir

        def manager = project.pluginManager

        project.buildscript {
            repositories {
                mavenLocal()
                jcenter()
                mavenCentral()
            }
            dependencies {
                //classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
                //classpath 'com.android.tools.build:gradle:2.0.0-alpha3'
            }
        }

        project.repositories {
            mavenLocal()
        }


        manager.apply('com.android.application')

        manager.apply(PandroidPlugin.class)

        //project.apply from: 'local.properties'
        project.android {
            compileSdkVersion 25
            buildToolsVersion "25.0.3"
        }
    }

    @After
    public void tearDown() {
        project = null;
        TestUtils.clearProjectDirectory();
    }

    @Test
    public void testAddExtensionToProject() {
        assert project.pandroid instanceof PandroidPluginExtension
    }

    @Test
    void testXMLMerger() {
        def testDir = new File(project.buildDir, "/test");
        testDir.mkdirs()
        File manifest = new File(testDir, "manifest.xml")
        manifest.delete()
        manifest.createNewFile()
        manifest << '''<?xml version="1.0" encoding="utf-8" ?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.leroymerlin.colibri.feedback">
<uses-permission android:name="android.permission.INTERNET"/>
<application
        android:name=".FeedbackApplication"
        android:allowBackup="true"
        android:icon="@mipmap/feedback_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity
            android:name=".login.LoginActivity"
            android:windowSoftInputMode="adjustResize">
        </activity>
        <activity android:name=".main.MainActivity" />
        <receiver
            android:name="com.google.android.gms.analytics.AnalyticsReceiver"
            android:enabled="true">
            <intent-filter>
                <action android:name="com.google.android.gms.analytics.ANALYTICS_DISPATCH" />
            </intent-filter>
        </receiver>
    </application>

</manifest>


'''

        XMLUtils.appendToXML(manifest, '''
<manifest>

    <uses-permission android:name="leroymerlin.permission.PASSPORT" />
    <application>
        <activity
            android:name=".main.MainActivity"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".login.LoginActivity" />

        <service
            android:name="com.google.android.gms.analytics.AnalyticsService"
            android:enabled="true"
            android:exported="false" />

        <meta-data
            android:name="io.fabric.ApiKey"
            android:value="6e889750355dbdb32baad502085da096f384c6bb" />
    </application>

</manifest>

''')

        def parser = new XmlSlurper()
        def xml = parser.parse(manifest)
        assert xml.application.service.'@android:name' == "com.google.android.gms.analytics.AnalyticsService"
        assert xml.application.size() == 1
        assert xml.application.activity.size() == 2
        assert xml.application.activity.'intent-filter'.action.'@android:name' == "android.intent.action.MAIN"

    }

    // @Test
    public void testAddCompileDependenciesToProject() {

        project.pandroid {
            library 'vision'
        }
        project.evaluate()

        boolean visionDependencyInjected = false;
        project.getConfigurations().getByName("compile").getDependencies().each {
            dep ->
                if (dep.name.contains('play-services-vision')) {
                    visionDependencyInjected = true;
                };
        }

        assert visionDependencyInjected;

    }

    @Test
    public void testAESEncryption() {

        def key = "azertyuiopetzjdl"
        def value = "asxdertgbvdyhjhfdrtgvcx"
        String encrypt = AESEncryption.symetricEncrypt(key, value);
        String decrypt = AESEncryption.symetricDecrypt(key, encrypt);

        assert !value.equals(encrypt)
        assert value.equals(decrypt)

    }

    @Test
    public void testAddSecureProperties() {

        project.pandroid {
            productFlavors {

                all {
                    secureField "key1", "encryptedKey"
                }
                azerty {
                    secureField "key2", "azerty"
                }

            }
        }
        project.evaluate()
        assert project.android.productFlavors.azerty != null
    }
}
