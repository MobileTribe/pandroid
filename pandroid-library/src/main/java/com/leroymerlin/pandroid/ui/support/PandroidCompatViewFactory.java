package com.leroymerlin.pandroid.ui.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.RestrictTo;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ToggleButton;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Created by florian on 03/01/2018.
 */

public class PandroidCompatViewFactory implements LayoutInflater.Factory2 {
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return onCreateView(name, context, attrs);
    }

    @Override
    @SuppressLint("RestrictedApi")
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        final Resources.Theme theme = context.getTheme();
        switch (name) {
            case "Button":
                return new PandroidCompatButton(new ContextThemeWrapper(context, theme), attrs);
            case "EditText":
                return new PandroidCompatEditText(new ContextThemeWrapper(context, theme), attrs);
            case "RadioButton":
                return new PandroidCompatEditText(new ContextThemeWrapper(context, theme), attrs);
            case "Switch":
                return new PandroidCompatSwitch(new ContextThemeWrapper(context, theme), attrs);
            case "TextView":
                return new PandroidCompatTextView(new ContextThemeWrapper(context, theme), attrs);
            case "ToggleButton":
                return new ToggleButton(new ContextThemeWrapper(context, theme), attrs);
            default:
                return null;
        }
    }
}
