package com.leroymerlin.pandroid.event.opener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by Mehdi on 20/06/2017.
 */

public class ActivityOpener extends Opener<Activity> {
    private static final long serialVersionUID = -1234536898054135590L;
    private int resultCode;

    public ActivityOpener(Class<? extends Activity> activityClass) {
        super(activityClass);
    }

    @NonNull
    public Intent newInstance(Context context) {
        Intent intent = new Intent(context, getComponentClass());
        intent.putExtras(getArguments());
        return intent;
    }

    @Nullable
    public static <T extends ActivityOpener> T getOpener(@NonNull Activity activity) {
        Intent intent = activity.getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            return getOpener(extras);
        }
        return null;
    }

    public int getResultCode() {
        return resultCode;
    }
}
