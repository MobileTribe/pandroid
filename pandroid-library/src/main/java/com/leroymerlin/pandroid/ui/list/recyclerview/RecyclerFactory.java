
package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface RecyclerFactory<T> {
    public T create(LayoutInflater inflater, ViewGroup parent, int viewType);
}
