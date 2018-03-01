
package com.leroymerlin.pandroid.ui.list.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Holder<T> {

    View getView(LayoutInflater inflater, ViewGroup parent, T content);

    void setContent(T content, int index);
}
