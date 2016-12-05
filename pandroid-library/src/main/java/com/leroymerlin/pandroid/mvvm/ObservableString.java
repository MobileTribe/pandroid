package com.leroymerlin.pandroid.mvvm;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by adrien on 08/09/16.
 */
public class ObservableString extends BaseObservable implements Parcelable, Serializable {
    static final long serialVersionUID = 1L;
    private String mValue;

    /**
     * Creates an ObservableLong with the given initial value.
     *
     * @param value the initial value for the ObservableLong
     */
    public ObservableString(String value) {
        mValue = value;
    }

    /**
     * Creates an ObservableLong with the initial value of <code>0L</code>.
     */
    public ObservableString() {
    }

    /**
     * @return the stored value.
     */
    public String get() {
        return mValue;
    }

    /**
     * Set the stored value.
     */
    public void set(String value) {
        if (value == null || !value.equals(mValue)) {
            mValue = value;
            notifyChange();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mValue);
    }

    public static final Parcelable.Creator<ObservableString> CREATOR
            = new Parcelable.Creator<ObservableString>() {

        @Override
        public ObservableString createFromParcel(Parcel source) {
            return new ObservableString(source.readString());
        }

        @Override
        public ObservableString[] newArray(int size) {
            return new ObservableString[size];
        }
    };
}