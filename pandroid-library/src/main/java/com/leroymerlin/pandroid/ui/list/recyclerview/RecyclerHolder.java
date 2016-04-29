package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by florian on 19/11/14.
 */
public abstract class RecyclerHolder<T> extends RecyclerView.ViewHolder {


    public RecyclerHolder(View view) {
        super(view);
        setView(view);
    }

    protected void setView(View view) {

    }

    public abstract void setContent(T content, int index);
}
