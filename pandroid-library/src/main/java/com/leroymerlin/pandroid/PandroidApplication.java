package com.leroymerlin.pandroid;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.leroymerlin.pandroid.app.PandroidConfig;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.ButterKnifeLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.EventBusLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.IcepickLifecycleDelegate;
import com.leroymerlin.pandroid.dagger.BaseComponent;
import com.leroymerlin.pandroid.dagger.DaggerPandroidComponent;
import com.leroymerlin.pandroid.dagger.PandroidModule;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.LogcatLogger;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.lang.reflect.Method;

import javax.inject.Inject;

import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by florian on 04/12/15.
 */
public class PandroidApplication extends Application {


    private static final String TAG = "PandroidApplication";

    protected LogWrapper logWrapper;

    protected BaseComponent mBaseComponent;

    @Inject
    EventBusManager eventBusManager;


    @Override
    public void onCreate() {
        super.onCreate();
        initializeBuildConfig();
        initializeLogger();
        inject(this);
    }

    //tag::Logger[]
    protected void initializeLogger() {
        logWrapper = PandroidLogger.getInstance();
        logWrapper.addLogger(LogcatLogger.getInstance());
    }
    //end::Logger[]

    public void inject(Object obj) {
        if (obj != null) {
            BaseComponent baseComponent = getBaseComponent();

            invokeInject(baseComponent, obj, obj.getClass());
        }
    }


    private void invokeInject(BaseComponent baseComponent, Object obj, Class objClass) {
        try {
            Method method = baseComponent.getClass().getMethod("inject", objClass);
            method.invoke(baseComponent, obj);
        } catch (NoSuchMethodException e) {
            if (objClass.getSuperclass() != null) {
                invokeInject(baseComponent, obj, objClass.getSuperclass());
            } else {
                throw new IllegalStateException("inject(" + obj.getClass().getSimpleName() + ") is not declared in your BaseComponent", e);
            }
        } catch (Exception e) {
            logWrapper.e(TAG, e);
            throw new RuntimeException("Can't inject in " + obj.getClass()+ "  with your BaseComponent ", e);
        }
    }


    @VisibleForTesting
    public void overrideBaseComponent(BaseComponent mBaseComponent) {
        this.mBaseComponent = mBaseComponent;
    }

    protected BaseComponent getBaseComponent() {
        if (mBaseComponent == null) {
            mBaseComponent = createBaseComponent();
        }
        return mBaseComponent;
    }


    public static PandroidApplication get(Context context) {
        return (PandroidApplication) context.getApplicationContext();
    }

    public Class getBuildConfig() throws ClassNotFoundException {
        String buildConfigName = getPackageName() + ".BuildConfig";
        return Class.forName(buildConfigName);
    }

    public void initializeBuildConfig() {
        try {
            Class buildConfig = getBuildConfig();
            PandroidConfig.DEBUG = (Boolean) buildConfig.getField("DEBUG").get(buildConfig); //NoSuchFieldException
            PandroidConfig.APPLICATION_ID = (String) buildConfig.getField("APPLICATION_ID").get(buildConfig);
            PandroidConfig.BUILD_TYPE = (String) buildConfig.getField("BUILD_TYPE").get(buildConfig);
            PandroidConfig.FLAVOR = (String) buildConfig.getField("FLAVOR").get(buildConfig);
            PandroidConfig.VERSION_CODE = (Integer) buildConfig.getField("VERSION_CODE").get(buildConfig);
            PandroidConfig.VERSION_NAME = (String) buildConfig.getField("VERSION_NAME").get(buildConfig);
        } catch (Exception e) {
            Log.e(TAG, "Can't find BuildConfig var, override getBuildConfig to fix this", e);
        }
    }


    /**
     * Initialize app base component. Override this methode to give your own component and provide
     * new manager to PandroidFragment/PandroidActivity
     *
     * @return BaseComponent that will be used to inject manager in Pandroid Classes
     */
    protected BaseComponent createBaseComponent() {
        return DaggerPandroidComponent.builder()
                .pandroidModule(new PandroidModule(this))
                .build();
    }

    //tag::PandroidBaseLifecycleDelegate[]

    /**
     * initialize LifecycleDelegate that will listen the activity life cycle
     * Override this method to disable or add more delegate
     *
     * @return PandroidDelegate that will be used in pandroid activity / pandroid fragment
     */
    public PandroidDelegate createBasePandroidDelegate() {
        PandroidDelegate pandroidDelegate = new PandroidDelegate();
        pandroidDelegate.addLifecycleDelegate(new EventBusLifecycleDelegate(eventBusManager));
        try {
            Class.forName(ButterKnife.class.getName());
            pandroidDelegate.addLifecycleDelegate(new ButterKnifeLifecycleDelegate());
        } catch (ClassNotFoundException e) {
            logWrapper.v(TAG, "ButterKnife is disabled, add the library in Pandroid extension to use it");
        }
        try {
            Class.forName(Icepick.class.getName());
            pandroidDelegate.addLifecycleDelegate(new IcepickLifecycleDelegate());
        } catch (ClassNotFoundException e) {
            logWrapper.v(TAG, "Icepick is disabled, add the library in Pandroid extension to use it");
        }
        return pandroidDelegate;
    }
    //tag::PandroidBaseLifecycleDelegate[]

}
