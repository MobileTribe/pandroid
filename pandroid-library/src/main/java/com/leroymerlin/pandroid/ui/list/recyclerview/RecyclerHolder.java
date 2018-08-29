package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.R;

/**
 * Created by florian on 19/11/14.
 */
public abstract class RecyclerHolder<? extends T> extends RecyclerView.ViewHolder {

    /**
     * Constructor used to instantiate the older without layoutId
     * See HolderFactory impl for details
     *
     * @param inflater
     * @param parent
     */
    public RecyclerHolder(LayoutInflater inflater, ViewGroup parent) {
        super(new View(parent.getContext()));
        throw new IllegalStateException("This constructor must be override to be initialized by HolderFactory. See documentation");
    }

    public RecyclerHolder(View view) {
        super(view);
        bindView(view);
    }

    protected void bindView(View view) {

    }

    public abstract void setContent(T content, int index);
}


