package com.leroymerlin.pandroid.mvvm;

import android.databinding.BindingConversion;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.leroymerlin.pandroid.R;

/**
 * Created by adrien on 08/09/16.
 */
public class PandroidBindingAdapter {

    public PandroidBindingAdapter() {
    }

    @android.databinding.BindingAdapter({"error"})
    public static void bindErrorEditText(final EditText view, final BindableString bindableString) {
        if (bindableString != null && !TextUtils.isEmpty(bindableString.get())) {
            view.setError(bindableString.get());
        }
    }

    @android.databinding.BindingAdapter({"binding"})
    public static void bindEditText(final EditText view, final BindableString bindableString) {
        // We use tag to ensure that we aren't adding multiple TextWatcher for same EditText. This ensures that
        // EditText has only one TextWatcher
        if (view.getTag(R.id.dataBinding) == null) {
            view.setTag(R.id.dataBinding, true);
            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    bindableString.set(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        // Checking if the value has really changed. This prevents problems with the position of cursor
        // in the EditText
        String newValue = bindableString.get();
        if (!view.getText().toString().equals(newValue)) {
            view.setText(newValue);
        }

    }

    @BindingConversion
    public static String convertObservableStringToString(ObservableField<String> observableString) {
        return observableString.get();
    }

}
