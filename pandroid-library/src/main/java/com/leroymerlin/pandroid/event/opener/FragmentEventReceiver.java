package com.leroymerlin.pandroid.event.opener;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.leroymerlin.pandroid.log.LogWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by florian on 12/11/14.
 */
public class FragmentEventReceiver<T extends OpenerReceiverProvider> extends OpenerEventReceiver<T, FragmentOpener>{

    private final static String TAG = "FragmentEventReceiver";

    // ###### FRAGMENT OPENING PART #######

    public static final int[] ANIM_SLIDE = {R.animator.pandroid_in_right, R.animator.pandroid_out_left,
            R.animator.pandroid_in_left, R.animator.pandroid_out_right};
    public static final int[] ANIM_SLIDE_TOP = {R.animator.pandroid_in_top, R.animator.pandroid_out_bottom,
            R.animator.pandroid_in_bottom, R.animator.pandroid_out_top};
    public static final int[] ANIM_SLIDE_BOTTOM = {R.animator.pandroid_in_bottom, R.animator.pandroid_out_top,
            R.animator.pandroid_in_top, R.animator.pandroid_out_bottom};
    public static final int[] ANIM_FADE = {R.animator.pandroid_fade_in, R.animator.pandroid_fade_out,
            R.animator.pandroid_fade_in, R.animator.pandroid_fade_out};
    public static final int[] ANIM_MATERIAL = {0, R.animator.pandroid_material_enter_out, 0, R.animator.pandroid_material_exit_out};


    private int[] anim = null;
    private int fragmentContainerId;
    private String backStackTag;
    private String defaultBreadcrumbTitle;
    private String fragmentTag;
    private boolean clearBackStack;


    @Override
    protected void onOpenerReceived(FragmentOpener fragmentOpener) {

        if (clearBackStack) {
            clearBackStack();
        }


        Fragment fragment = getFragment(fragmentOpener);
        if (fragment == null) {
            return;
        }

        T attachedObject = refAttachedObject.get();

        if (attachedObject == null) {
            return;
        }


        FragmentTransaction trans = ((T) attachedObject).provideFragmentManager().beginTransaction();
        if (anim != null && anim.length > 3)
            trans.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);

        if (backStackTag != null) {
            trans.addToBackStack(backStackTag);
        }

        if (fragmentOpener.getTitle() != null) {
            trans.setBreadCrumbTitle(fragmentOpener.getTitle());
        } else if (defaultBreadcrumbTitle != null) {
            trans.setBreadCrumbTitle(defaultBreadcrumbTitle);
        }

        boolean handled = onExecuteFragmentTransaction(fragmentOpener, fragment, trans);
        if (!handled) {
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).show(trans, fragmentTag);
            } else {
                trans.replace(fragmentContainerId, fragment, fragmentTag);
                try {
                    trans.commit();
                } catch (IllegalStateException e) {
                    Log.w(TAG, "Commit failed", e);
                }
            }
        }


    }

    /**
     * Called just before the transaction commit
     *
     * @param fragmentOpener event received
     * @param fragment       fragment that will be opened
     * @param trans          pending transaction
     * @return true to cancel the transaction, false otherwise
     */
    protected boolean onExecuteFragmentTransaction(FragmentOpener fragmentOpener, Fragment fragment, FragmentTransaction trans) {
        return false;
    }

    /**
     * remove immediately all fragments in backStack
     */
    protected void clearBackStack() {
        T attachedObject = refAttachedObject.get();
        if (attachedObject != null && attachedObject.provideFragmentManager().getBackStackEntryCount() > 0) {
            attachedObject.provideFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


    protected Fragment getFragment(FragmentOpener opener) {
        try {
            return opener.newInstance();
        } catch (InstantiationException e) {
            logWrapper.e(TAG, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logWrapper.e(TAG, e.getMessage(), e);
        }
        return null;
    }


    public FragmentEventReceiver setContainerId(int fragmentContainerId) {
        this.fragmentContainerId = fragmentContainerId;
        return this;
    }

    public FragmentEventReceiver addFragment(Class<? extends PandroidFragment> fragmentClass) {
        addFragment(new FragmentOpener(fragmentClass));
        return this;
    }

    public FragmentEventReceiver addFragment(FragmentOpener opener) {
        filter.add(opener.getFilterTag());
        return this;
    }

    public FragmentEventReceiver setFragmentTag(String fragmentTag) {
        this.fragmentTag = fragmentTag;
        return this;
    }

    public FragmentEventReceiver clearBackStackOnOpening(boolean clearBackStack) {
        this.clearBackStack = clearBackStack;
        return this;
    }

    public FragmentEventReceiver setAnim(int[] anim) {
        this.anim = anim;
        return this;
    }

    public FragmentEventReceiver setBackStackTag(String backStackTag) {
        this.backStackTag = backStackTag;
        return this;

    }

    public FragmentEventReceiver setDefaultBreadcrumbTitle(String breadcrumbTitle) {
        this.defaultBreadcrumbTitle = breadcrumbTitle;
        return this;
    }



}
