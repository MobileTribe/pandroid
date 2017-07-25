package com.leroymerlin.pandroid.event.opener;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.app.PandroidFragment;

/**
 * Created by florian on 12/11/14.
 */
public class ActivityEventReceiver<T extends OpenerReceiverProvider> extends OpenerEventReceiver<T, ActivityOpener> {

    private final static String TAG = "ActivityEventReceiver";
    private int flags;
    private boolean finishActivity;
    private int[] animations;


    public ActivityEventReceiver overrideAnimation(int[] animations) {
        this.animations = animations;
        return this;
    }

    public ActivityEventReceiver addFlags(int flags) {
        this.flags = flags | this.flags;
        return this;
    }

    public ActivityEventReceiver setFinishActivity(boolean finish) {
        this.finishActivity = finish;
        return this;
    }

    @Override
    protected void onOpenerReceived(ActivityOpener opener) {
        T attachedObject = this.refAttachedObject.get();
        if (attachedObject == null) {
            logWrapper.w(TAG, "ActivityOpener ignore because the attachedObject was null in the receiver " + this.getClass().getSimpleName());
            return;
        }

        Intent intent = createIntent(opener, attachedObject.provideActivity());
        applyFlags(opener, intent);
        Activity parentActivity = attachedObject.provideActivity();
        boolean handled = onExecuteActivityOpen(opener, intent);
        if (!handled) {
            if (animations != null && animations.length == 2) {
                parentActivity.overridePendingTransition(animations[0], animations[1]);
            }
            int resultCode = opener.getResultCode();
            if (resultCode > 0) {
                parentActivity.startActivityForResult(intent, resultCode);
            } else {
                parentActivity.startActivity(intent);
            }
            if (finishActivity) {
                parentActivity.finish();
            }
        }
    }

    /**
     * Called just before the activity is started. Override to customize or cancel the opening
     *
     * @param opener opener received
     * @param intent intent that will be send received
     * @return true to cancel the opening, false otherwise
     */
    protected boolean onExecuteActivityOpen(ActivityOpener opener, Intent intent) {
        return false;
    }

    protected Intent createIntent(ActivityOpener opener, Context context) {
        return opener.newInstance(context);
    }

    protected void applyFlags(ActivityOpener opener, Intent intent) {
        intent.setFlags(flags);
    }


}
