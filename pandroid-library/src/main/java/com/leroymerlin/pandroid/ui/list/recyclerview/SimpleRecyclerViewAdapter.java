package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by florian on 19/11/14.
 */
public class SimpleRecyclerViewAdapter<T> extends RecyclerViewAdapter<T> {

    public SimpleRecyclerViewAdapter(final SimpleHolder<T> simpleHolder) {
        super(new RecyclerFactory<RecyclerHolder<T>>() {
            @Override
            public RecyclerHolder<T> create(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return new RecyclerHolder<T>(inflater.inflate(simpleHolder.resLayout, parent, false)) {
                    public View view;

                    @Override
                    protected void setView(View view) {
                        super.setView(view);
                        this.view = view;
                    }

                    @Override
                    public void setContent(T content, int index) {
                        simpleHolder.setContent(content, view, index);
                    }
                };
            }
        });
    }

    public static abstract class SimpleHolder<T> {


        private final int resLayout;

        public SimpleHolder(@LayoutRes final int resLayout) {
            this.resLayout = resLayout;
        }

        public abstract void setContent(T content, View view, int index);
    }

}