package com.leroymerlin.pandroid.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegateProvider;
import com.leroymerlin.pandroid.dagger.BaseComponent;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.OnBackListener;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.leroymerlin.pandroid.event.opener.ActivityOpener;
import com.leroymerlin.pandroid.event.opener.FragmentOpener;
import com.leroymerlin.pandroid.event.opener.OpenerReceiverProvider;
import com.leroymerlin.pandroid.future.Cancellable;
import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.ui.PandroidViewFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by florian on 05/11/14.
 * <p/>
 * PandroidActivity is a Activity that simplify the activity cycle of life by introducing onResume(ResumeState) method.
 * This Activity handle support toolbar. You just have to use LinearToolbarLayout as root view or an equivalent view.
 * Back event and fragment back stack changed are treated to.
 * If static field TAG is set PandroidActivity inject Broadcast receiver himself
 */
@RxWrapper
public class PandroidActivity<T extends ActivityOpener> extends AppCompatActivity implements Cancellable.CancellableRegister, OpenerReceiverProvider, PandroidDelegateProvider {

    protected LogWrapper logWrapper;
    protected EventBusManager eventBusManager;

    //tag::PandroidActivityInjection[]
    //@Inject
    //AnyThingInBaseComponent instance;
    //end::PandroidActivityInjection[]

    protected T opener;

    protected PandroidDelegate pandroidDelegate;

    //tag::PandroidActivityInjection[]
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //override layout inflater factory if a custom factory is set
        PandroidViewFactory.installPandroidViewFactory(this);
        super.onCreate(savedInstanceState);
        //initialize PandroidDelegate with the default from PandroidApplication
        BaseComponent baseComponent = PandroidApplication.getInjector(this).getBaseComponent();
        logWrapper = baseComponent.logWrapper();
        eventBusManager = baseComponent.eventBusManager();
        pandroidDelegate = createDelegate();
        pandroidDelegate.onInit(this);
        opener = ActivityOpener.getOpener(this);
    }

    //end::PandroidActivityInjection[]
    @Override
    public PandroidDelegate getPandroidDelegate() {
        return pandroidDelegate;
    }

    protected PandroidDelegate createDelegate() {
        if (getApplication() instanceof PandroidDelegateProvider) {
            return ((PandroidDelegateProvider) getApplication()).getPandroidDelegate();
        }
        return new PandroidDelegate();
    }

    @CallSuper
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        pandroidDelegate.onCreateView(this, findViewById(android.R.id.content), savedInstanceState);
        if (opener != null && opener.getTitle() != null && !opener.getTitle().isEmpty() && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(opener.getTitle());
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        pandroidDelegate.onResume(this);
        ResumeState state = pandroidDelegate.getResumeState();
        onResume(state);
    }

    //tag::PandroidActivityResume[]

    /**
     * call at the end of onResume process. This method will help you determine the king of resume
     * your activity is facing
     *
     * @param state current resume stat of the activity
     */
    protected void onResume(ResumeState state) {
        logWrapper.i(getClass().getSimpleName(), "resume state: " + state);
    }
    //end::PandroidActivityResume[]

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        pandroidDelegate.onPause(this);
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        pandroidDelegate.onSaveView(this, outState);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pandroidDelegate.onDestroyView(this);
    }

    public Fragment getCurrentFragment() {
        return null;
    }

    @Override
    public void registerDelegate(Cancellable delegate) {
        pandroidDelegate.registerDelegate(delegate);
    }

    @Override
    public boolean unregisterDelegate(Cancellable delegate) {
        return pandroidDelegate.unregisterDelegate(delegate);
    }


    //tag::PandroidActivityBack[]
    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment(); //override this methode to give your current fragment
        if (fragment != null && fragment instanceof OnBackListener && ((OnBackListener) fragment).onBackPressed()) {
            //back handle by current fragment
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            //back handle by getFragmentManager
            getSupportFragmentManager().popBackStack();
        } else {
            onBackExit();
        }
    }

    //end::PandroidActivityBack[]

    private boolean existOnBack = false;

    private void onBackExit() {
        if (!existOnBack && showExitMessage()) {
            existOnBack = true;
        } else {
            super.onBackPressed();
        }
    }

    //tag::PandroidActivityBack[]

    /**
     * Override this method to show an exit message and enable back confirmation to exit
     *
     * @return true to stop the app exit, false otherwise
     */
    protected boolean showExitMessage() {
        return false;
    }
    //end::PandroidActivityBack[]


    //tag::PandroidActivityReceivers[]

    /**
     * Override this method to automatically (un)register receiver to the event bus with the activity life cycle
     *
     * @return list of receivers attached by EventBusLifecycleDelegate
     */
    @Override
    public List<EventBusManager.EventBusReceiver> getReceivers() {
        if (getApplication() instanceof ReceiversProvider)
            return ((ReceiversProvider) getApplication()).getReceivers();
        return new ArrayList<>();
    }
    //end::PandroidActivityReceivers[]

    @Override
    public FragmentManager provideFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    public Activity provideActivity() {
        return this;
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        sendEventSync(new ActivityOpener(activityClass));
    }

    public void startFragment(Class<? extends Fragment> fragmentClass) {
        sendEventSync(new FragmentOpener(fragmentClass));
    }

    public void sendEvent(Object event) {
        eventBusManager.send(event);
    }

    public void sendEventSync(Object event) {
        eventBusManager.sendSync(event);
    }

}
