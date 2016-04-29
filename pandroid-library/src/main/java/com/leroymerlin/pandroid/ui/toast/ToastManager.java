package com.leroymerlin.pandroid.ui.toast;


import android.app.Activity;
import android.os.Bundle;

import com.leroymerlin.pandroid.R;

import java.util.Collection;


/**
 * Created by paillardf on 06/03/2014.
 */
public interface ToastManager {

    public final static int LONG_DURATION = 6000;
    public final static int NORMAL_DURATION = 3000;
    public final static int SHORT_DURATION = 2000;

    public void onSaveInstanceState(Bundle outState);

    public void onRestoreState(Bundle outState, Activity activity, Collection<ToastListener> listenerWrappers);

    public ToastNotifier makeToast(Activity activity, String text, ToastListener listener);

    public ToastNotifier makeToast(Activity activity, String text, ToastListener listener, int style);

    public ToastNotifier makeToast(Activity activity, String text, ToastListener listener, int style, int duration);

    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, ToastListener listener);

    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, ToastListener listener, int style);

    public ToastNotifier makeImageToast(Activity activity, String text, int drawableResource, ToastListener listener, int style, int duration);

    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, ToastListener listener);

    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, ToastListener listener, int style);

    public ToastNotifier makeActionToast(Activity activity, String text, String buttonText, int icon, ToastListener listener, int style, int duration);

    public ToastNotifier makeLoaderToast(Activity activity, String text, boolean undefinedLoad, ToastListener listener);

    public ToastNotifier makeLoaderToast(Activity activity, String text, boolean undefinedLoad, ToastListener listener, int style);

    public ToastNotifier makeLoaderToast(Activity activity, String text, boolean undefinedLoad, ToastListener listener, int style, int duration);

    public ToastBuilder builder();

    public interface ToastNotifier {
        public void setProgress(int progress);

        public void dismiss();
    }

    public abstract class ToastListener {

        private String tag;

        public String getTag() {
            return tag;
        }

        public ToastListener() {
            this("ToastListener");
        }

        public ToastListener(String tag) {
            this.tag = tag;
        }

        public abstract void onDismiss();

        public abstract void onActionClicked();

    }


    abstract class ToastBuilder {

        protected ToastType type = ToastType.NORMAL;
        protected int duration = NORMAL_DURATION;
        protected ToastListener listener;
        protected int textId;
        protected String text;
        protected boolean loadUndefined;
        protected int drawable;
        protected int btnTextId;
        protected String btnText;
        protected int style = R.style.Toast;

        public ToastBuilder setType(ToastType type) {
            this.type = type;
            return this;
        }

        public ToastBuilder setStyle(int style) {
            this.style = style;
            return this;
        }

        public ToastBuilder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public ToastBuilder setListener(ToastListener listener) {
            this.listener = listener;
            return this;
        }

        public ToastBuilder setText(int textId) {
            this.textId = textId;
            return this;
        }

        public ToastBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public ToastBuilder setButtonText(int btnTextId) {
            setType(ToastType.ACTION);
            this.btnTextId = btnTextId;
            return this;
        }

        public ToastBuilder setButtonText(String btnText) {
            setType(ToastType.ACTION);
            this.btnText = btnText;
            return this;
        }

        public ToastBuilder setLoadUndefined(boolean loadUndefined) {
            setType(ToastType.LOADER);
            this.loadUndefined = loadUndefined;
            return this;
        }

        public ToastBuilder setDrawable(int drawable) {
            this.drawable = drawable;
            return this;
        }

        public abstract ToastNotifier show(Activity activity);
    }

    enum ToastType {
        NORMAL,
        ACTION,
        LOADER
    }

}
