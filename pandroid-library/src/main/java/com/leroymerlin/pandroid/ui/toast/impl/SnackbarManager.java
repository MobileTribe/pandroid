package com.leroymerlin.pandroid.ui.toast.impl;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.ui.loader.ProgressWheel;
import com.leroymerlin.pandroid.ui.toast.ToastManager;
import com.leroymerlin.pandroid.utils.DeviceUtils;

import java.util.Collection;

/**
 * Created by florian on 27/09/2016.
 */

public class SnackbarManager implements ToastManager {

    private Snackbar lastShowNotif;

    public SnackbarManager() {
    }

    private ToastNotifier makeCustomNotification(Activity activity, ToastType toastType, String label, String btnLabel, int drawableRes, int style, int duration, boolean undefinedLoad, final ToastListener listener) {
        if (duration < 0)
            duration = Snackbar.LENGTH_INDEFINITE;
        final Snackbar notif = Snackbar.make(activity.findViewById(android.R.id.content), label, duration);
        if (style == 0) {
            style = R.style.Toast;
        }
        TypedArray attributes = activity.obtainStyledAttributes(style, R.styleable.ToastAppearance);
        int textColor = attributes.getColor(R.styleable.ToastAppearance_toastTextColor, ContextCompat.getColor(activity, R.color.white));
        int buttonTextColor = attributes.getColor(R.styleable.ToastAppearance_toastButtonTextColor, ContextCompat.getColor(activity, R.color.pandroid_green_dark));
        int backgroundColor = attributes.getColor(R.styleable.ToastAppearance_toastBackground, ContextCompat.getColor(activity, R.color.pandroid_green));
        notif.getView().setBackgroundColor(backgroundColor);
        ((TextView) notif.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
        TextView actionView = ((TextView) notif.getView().findViewById(android.support.design.R.id.snackbar_action));
        actionView.setTextColor(buttonTextColor);
        attributes.recycle();

        notif.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (listener != null)
                    listener.onDismiss();
            }
        });

        Drawable drawable = null;
        if (drawableRes > 0) {
            drawable = ContextCompat.getDrawable(activity, drawableRes);
        }
        actionView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        if (toastType == ToastType.ACTION && btnLabel != null) {
            notif.setAction(btnLabel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onActionClicked();
                }
            });
        } else if (drawableRes > 0) {
            actionView.setVisibility(View.VISIBLE);
            actionView.setClickable(false);
            actionView.setFocusableInTouchMode(false);
            actionView.setFocusable(false);
            actionView.setEnabled(false);
        }

        if (toastType == ToastType.LOADER) {
            ProgressWheel progressWheel = new ProgressWheel(activity);
            progressWheel.setId(R.id.snakebar_loader);
            if (undefinedLoad)
                progressWheel.spin();

            progressWheel.setBarWidth((int) DeviceUtils.dpToPx(activity, 4));
            progressWheel.setCircleRadius((int) DeviceUtils.dpToPx(activity, 30));
            progressWheel.setBarColor(buttonTextColor);
            progressWheel.setLinearProgress(true);
            ((Snackbar.SnackbarLayout) notif.getView()).addView(progressWheel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        notif.show();
        lastShowNotif = notif;
        return new ToastNotifier() {
            @Override
            public void setProgress(int progress) {
                ProgressWheel loader = (ProgressWheel) notif.getView().findViewById(R.id.snakebar_loader);
                if (loader != null) {
                    loader.setProgress(progress / 100f);
                }
            }

            @Override
            public void dismiss() {
                notif.dismiss();
            }

        };
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        SuperActivityToast.onSaveState(outState);
    }

    @Override
    public void onRestoreState(Bundle outState, Activity activity, Collection<ToastListener> TaggedListener) {
    }

    @Override
    public ToastNotifier makeToast(Activity activity, String text, ToastListener listener) {
        return makeToast(activity, text, listener, R.style.Toast);
    }

    @Override
    public ToastNotifier makeToast(Activity activity, String text, ToastListener listener, int style) {
        return makeToast(activity, text, listener, style, activity.getResources().getInteger(R.integer.toast_normal_duration));
    }

    @Override
    public ToastNotifier makeToast(Activity activity, String text, ToastListener listener, int style, int duration) {
        return makeCustomNotification(activity, ToastType.NORMAL, text, null, -1, style, duration, false, listener);
    }

    @Override
    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, ToastListener listener) {
        return makeImageToast(activity, text, drawableResource, listener, R.style.Toast);
    }

    @Override
    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, ToastListener listener, int style) {
        return makeImageToast(activity, text, drawableResource, listener, style, activity.getResources().getInteger(R.integer.toast_normal_duration));
    }

    @Override
    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, final ToastListener listener, int style, int duration) {
        return makeActionToast(activity, text, null, drawableResource, listener, style, duration);
    }

    @Override
    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, ToastListener listener) {
        return makeActionToast(activity, text, buttonText, icon, listener, R.style.Toast);
    }

    @Override
    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, ToastListener listener, int style) {
        return makeActionToast(activity, text, buttonText, icon, listener, style, -1);
    }

    @Override
    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, final ToastListener listener, int style, int duration) {
        return makeCustomNotification(activity, ToastType.ACTION, text, buttonText, icon, style, duration, false, listener);
    }

    @Override
    public ToastNotifier makeLoaderToast(Activity activity, String text, boolean undefinedLoad, ToastListener listener) {
        return makeLoaderToast(activity, text, undefinedLoad, listener, R.style.Toast);
    }

    @Override
    public ToastNotifier makeLoaderToast(Activity activity, String text, boolean undefinedLoad, ToastListener listener, int style) {
        return makeLoaderToast(activity, text, undefinedLoad, listener, style, -1);
    }

    @Override
    public ToastNotifier makeLoaderToast(Activity activity, String text, boolean undefinedLoad, ToastListener listener, int style, int duration) {
        return makeCustomNotification(activity, ToastType.LOADER, text, null, -1, style, duration, undefinedLoad, listener);
    }

    @Override
    public ToastBuilder builder() {
        return new ToastBuilder() {
            @Override
            public ToastNotifier show(Activity activity) {
                if (text == null && textId != 0)
                    text = activity.getString(textId);
                if (btnText == null && btnTextId != 0)
                    btnText = activity.getString(btnTextId);

                switch (type) {
                    case NORMAL:
                        if (drawable != 0)
                            return makeImageToast(activity, text, drawable, listener, style, duration);
                        else
                            return makeToast(activity, text, listener, style, duration);
                    case ACTION:
                        return makeActionToast(activity, text, btnText, drawable, listener, style, duration);
                    case LOADER:
                        return makeLoaderToast(activity, text, loadUndefined, listener, style, duration);
                }
                return null;
            }
        };
    }

    @Override
    public void stopAllToast() {
        if (lastShowNotif != null)
            lastShowNotif.dismiss();
    }
}
