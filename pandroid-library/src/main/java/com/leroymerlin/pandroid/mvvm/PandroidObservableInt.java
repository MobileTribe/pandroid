package com.leroymerlin.pandroid.mvvm;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by adrien on 08/09/16.
 */
public class PandroidObservableInt extends BaseObservable implements Parcelable, Serializable {
    static final long serialVersionUID = 1L;
    private int mValue;

    /**
     * Creates an ObservableLong with the given initial value.
     *
     * @param value the initial value for the ObservableLong
     */
    public PandroidObservableInt(int value) {
        mValue = value;
    }

    /**
     * Creates an ObservableLong with the initial value of <code>0L</code>.
     */
    public PandroidObservableInt() {
    }

    /**
     * @return the stored value.
     */
    public int get() {
        return mValue;
    }

    /**
     * Set the stored value.
     */
    public void set(int value) {
        if (value != mValue) {
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
        dest.writeInt(mValue);
    }

    public static final Creator<PandroidObservableInt> CREATOR
            = new Creator<PandroidObservableInt>() {

        @Override
        public PandroidObservableInt createFromParcel(Parcel source) {
            return new PandroidObservableInt(source.readInt());
        }

        @Override
        public PandroidObservableInt[] newArray(int size) {
            return new PandroidObservableInt[size];
        }
    };
}