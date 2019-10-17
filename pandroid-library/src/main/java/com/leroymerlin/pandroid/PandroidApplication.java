package com.leroymerlin.pandroid;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.VisibleForTesting;

import com.leroymerlin.pandroid.app.PandroidConfig;
import com.leroymerlin.pandroid.app.PandroidMapper;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegateProvider;
import com.leroymerlin.pandroid.app.delegate.impl.AutoBinderLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.ButterKnifeLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.DaggerLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.EventBusLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.impl.IcepickLifecycleDelegate;
import com.leroymerlin.pandroid.app.delegate.rx.RxLifecycleDelegate;
import com.leroymerlin.pandroid.dagger.BaseComponent;
import com.leroymerlin.pandroid.dagger.DaggerPandroidComponent;
import com.leroymerlin.pandroid.dagger.PandroidInjector;
import com.leroymerlin.pandroid.dagger.PandroidModule;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.leroymerlin.pandroid.event.opener.ActivityEventReceiver;
import com.leroymerlin.pandroid.log.CrashlyticsLogger;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.LogcatLogger;
import com.leroymerlin.pandroid.log.PandroidLogger;
import com.leroymerlin.pandroid.ui.PandroidFactoryProvider;
import com.leroymerlin.pandroid.ui.support.PandroidCompatViewFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by florian on 04/12/15.
 */
public class PandroidApplication extends Application implements PandroidDelegateProvider, ReceiversProvider, PandroidInjector, PandroidFactoryProvider {


    private static final String TAG = "PandroidApplication";

    protected LogWrapper logWrapper;

    protected BaseComponent mBaseComponent;

    EventBusManager eventBusManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeBuildConfig();
        initializeLogger();
        this.eventBusManager = getBaseComponent().eventBusManager();
        inject(this);
    }

    //tag::Logger[]
    protected void initializeLogger() {
        logWrapper = PandroidLogger.getInstance();
        logWrapper.setDebug(PandroidConfig.DEBUG);
        logWrapper.addLogger(LogcatLogger.getInstance());
        if (PandroidConfig.isLibraryEnable("crashlytics")) {
            logWrapper.addLogger(new CrashlyticsLogger(this));
        }
    }
    //end::Logger[]

    @Override
    public void inject(Object obj) {
        if (obj != null) {
            BaseComponent baseComponent = getBaseComponent();
            PandroidMapper.getInstance().injectToTarget(baseComponent, obj);
        }
    }

    @Override
    public <T extends BaseComponent> T getBaseComponent() {
        if (mBaseComponent == null) {
            mBaseComponent = createBaseComponent();
        }
        return (T) mBaseComponent;
    }

    @VisibleForTesting
    public void overrideBaseComponent(BaseComponent mBaseComponent) {
        this.mBaseComponent = mBaseComponent;
    }

    public static <T extends PandroidInjector> T getInjector(Context context) {
        return (T) context.getApplicationContext();
    }

    public void initializeBuildConfig() {
        PandroidMapper.getInstance().setupConfig();
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
    protected PandroidDelegate createBasePandroidDelegate() {
        PandroidDelegate pandroidDelegate = new PandroidDelegate();
        pandroidDelegate.addLifecycleDelegate(new DaggerLifecycleDelegate());
        pandroidDelegate.addLifecycleDelegate(new EventBusLifecycleDelegate(eventBusManager));
        pandroidDelegate.addLifecycleDelegate(new AutoBinderLifecycleDelegate());
        if (PandroidConfig.isLibraryEnable("butterknife")) {
            pandroidDelegate.addLifecycleDelegate(new ButterKnifeLifecycleDelegate());
        } else {
            logWrapper.v(TAG, "ButterKnife is disabled, add the library in Pandroid extension to use it");
        }
        if (PandroidConfig.isLibraryEnable("icepick")) {
            pandroidDelegate.addLifecycleDelegate(new IcepickLifecycleDelegate());
        } else {
            logWrapper.v(TAG, "Icepick is disabled, add the library in Pandroid extension to use it");
        }
        if (PandroidConfig.isLibraryEnable("rxandroid")) {
            pandroidDelegate.addLifecycleDelegate(new RxLifecycleDelegate());
        } else {
            logWrapper.v(TAG, "RxAndroid is disabled, add the library in Pandroid extension to use it");
        }
        return pandroidDelegate;
    }

    @Override
    public PandroidDelegate getPandroidDelegate() {
        return createBasePandroidDelegate();
    }


    //end::PandroidBaseLifecycleDelegate[]

    /**
     * create list of receiver inject in Activity
     * You can override this method to inject activity receiver easily
     *
     * @return list of activity receivers inject in activity
     */
    protected List<ActivityEventReceiver> createBaseActivityReceivers() {
        return new ArrayList<>();
    }

    @Override
    public List<EventBusManager.EventBusReceiver> getReceivers() {
        return new ArrayList<EventBusManager.EventBusReceiver>(createBaseActivityReceivers());
    }

    /**
     * factories to use in the layout inflater. If pandroid view support is enable CompatViewFactory
     * will be added
     *
     * @return list of custom LayoutInflater factories
     */
    @Override
    public List<LayoutInflater.Factory2> getLayoutInflaterFactories() {
        ArrayList<LayoutInflater.Factory2> factories = new ArrayList<>();
        if (PandroidConfig.VIEW_SUPPORT) {
            factories.add(new PandroidCompatViewFactory());
        }
        return factories;
    }

}
