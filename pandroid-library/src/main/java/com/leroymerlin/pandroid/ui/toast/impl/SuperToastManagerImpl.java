package com.leroymerlin.pandroid.ui.toast.impl;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.ListenerUtils;
import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.core.content.ContextCompat;


/**
 * Created by paillardf on 06/03/2014.
 */
@Singleton
public class SuperToastManagerImpl implements ToastManager {


    @Inject
    public SuperToastManagerImpl() {
    }

    private SuperActivityToast makeCustomToast(Activity activity, int type, String text, int style, int duration) {
        SuperActivityToast toast = new SuperActivityToast(activity, type);
        toast.setText(text);
        applyStyle(toast, activity, style);
        if (duration > 0) {
            toast.setDuration(duration);
        } else {
            toast.setIndeterminate(true);
        }

        toast.setTouchToDismiss(true);

        toast.setAnimations(Style.ANIMATIONS_POP);
        return toast;
    }

    protected void applyStyle(SuperActivityToast toast, Activity activity, int style) {
        if (style == 0) {
            style = R.style.Toast;
        }
        TypedArray attributes = activity.obtainStyledAttributes(style, R.styleable.ToastAppearance);
        int textColor = attributes.getColor(R.styleable.ToastAppearance_toastTextColor, ContextCompat.getColor(activity, R.color.white));
        int backgroundColor = attributes.getColor(R.styleable.ToastAppearance_toastBackground, ContextCompat.getColor(activity, R.color.pandroid_green));
        toast.setColor(backgroundColor);
        toast.setTextColor(textColor);
        attributes.recycle();
    }

    private ToastNotifier getNotifier(final SuperActivityToast toast, final ToastListener listener) {

        if (listener != null) {
            toast.setOnDismissListener(listener.getTag(), new SuperToast.OnDismissListener() {
                @Override
                public void onDismiss(View view, Parcelable token) {
                    listener.onDismiss();
                }
            });

            toast.setOnButtonClickListener(listener.getTag(), null, new SuperActivityToast.OnButtonClickListener() {
                @Override
                public void onClick(View view, Parcelable token) {
                    listener.onActionClicked();
                }
            });


        }

        return new ToastNotifier() {
            @Override
            public void setProgress(int progress) {
                toast.setProgress(progress);
            }

            @Override
            public void dismiss() {
                toast.dismiss();
            }

        };

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SuperActivityToast.onSaveState(outState);
    }

    @Override
    public void onRestoreState(Bundle outState, Activity activity, Collection<ToastListener> TaggedListener) {
        ListenerUtils listenerUtils = ListenerUtils.newInstance();
        for (final ToastListener taggedListener : TaggedListener) {
            listenerUtils
                    .putListener(taggedListener.getTag(), new SuperActivityToast.OnButtonClickListener() {
                        @Override
                        public void onClick(View view, Parcelable token) {
                            ((ToastListener) taggedListener).onActionClicked();
                        }
                    })
                    .putListener(taggedListener.getTag(), new SuperToast.OnDismissListener() {
                        @Override
                        public void onDismiss(View view, Parcelable token) {
                            ((ToastListener) taggedListener).onDismiss();

                        }
                    });
            listenerUtils.getOnButtonClickListenerHashMap();

        }
        SuperActivityToast.onRestoreState(activity, outState, listenerUtils);
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
        final SuperActivityToast toast = makeCustomToast(activity, Style.TYPE_STANDARD, text, style, duration);
        ToastNotifier notifier = getNotifier(toast, listener);
        toast.show();
        return notifier;
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
    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, ToastListener listener, int style, int duration) {
        final SuperActivityToast toast = makeCustomToast(activity, Style.TYPE_STANDARD, text, style, duration);
        toast.setIconResource(Style.ICONPOSITION_LEFT, drawableResource);
        ToastNotifier notifier = getNotifier(toast, listener);
        toast.show();
        return notifier;
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
    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, ToastListener listener, int style, int duration) {
        SuperActivityToast toast = makeCustomToast(activity, Style.TYPE_BUTTON, text, style, duration);
        if (icon > 0)
            toast.setButtonIconResource(icon);
        toast.setButtonText(buttonText);
        ToastNotifier notifier = getNotifier(toast, listener);
        toast.show();
        return notifier;
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
        SuperActivityToast toast = makeCustomToast(activity, Style.TYPE_PROGRESS_CIRCLE, text, style, duration);
        toast.setProgressIndeterminate(undefinedLoad);
        ToastNotifier notifier = getNotifier(toast, listener);
        toast.show();
        return notifier;
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
        SuperToast.cancelAllSuperToasts();
    }
}
