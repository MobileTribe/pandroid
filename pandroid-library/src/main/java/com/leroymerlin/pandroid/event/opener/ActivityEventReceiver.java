package com.leroymerlin.pandroid.event.opener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by florian on 12/11/14.
 */
public class ActivityEventReceiver<T extends OpenerReceiverProvider> extends OpenerEventReceiver<T, ActivityOpener> {

    private final static String TAG = "ActivityEventReceiver";
    private int flags;
    private boolean finishActivity;
    private int[] animations;


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

            int resultCode = opener.getResultCode();
            if (resultCode > 0) {
                parentActivity.startActivityForResult(intent, resultCode);
            } else {
                parentActivity.startActivity(intent);
            }

            if (animations != null && animations.length == 2) {
                parentActivity.overridePendingTransition(animations[0], animations[1]);
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


    public ActivityEventReceiver<T> overrideAnimation(int[] animations) {
        this.animations = animations;
        return this;
    }

    public ActivityEventReceiver<T> addFlags(int flags) {
        this.flags = flags | this.flags;
        return this;
    }

    public ActivityEventReceiver<T> setFinishActivity(boolean finish) {
        this.finishActivity = finish;
        return this;
    }

    public ActivityEventReceiver<T> addActivity(ActivityOpener opener) {
        return this.add(opener);
    }

    public ActivityEventReceiver<T> addActivity(Class<? extends Activity> activityClass) {
        return this.add(activityClass);
    }

}
