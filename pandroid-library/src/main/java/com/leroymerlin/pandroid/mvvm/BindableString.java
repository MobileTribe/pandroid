package com.leroymerlin.pandroid.mvvm;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Objects;

/**
 * Created by adrien on 08/09/16.
 */
public class BindableString extends BaseObservable {

    String value;

    // Function to get the value
    public String get() {
        return value != null ? value : "";
    }

    // Function to set the value
    public void set(@NonNull String value) {
        // We need to have Object.equals since values is null for the first time
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            notifyChange();
        }
    }

    // Function to check if value is empty
    public boolean isEmpty() {
        return TextUtils.isEmpty(value);
    }
}
