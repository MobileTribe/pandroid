package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

public abstract class HolderFactory<T> {

    public abstract RecyclerHolder<? extends T> createHolder(LayoutInflater inflater, ViewGroup parent);

    public interface HolderBinder<T> {
        void bindHolder(View view, T data, int index);
    }

    public static <T> HolderFactory<T> create(Class<? extends RecyclerHolder<? extends T>> holderClass) {
        try {
            final Constructor<? extends RecyclerHolder<? extends T>> constructor = holderClass.getConstructor(LayoutInflater.class, ViewGroup.class);
            return new HolderFactory<T>() {
                @Override
                public RecyclerHolder<? extends T> createHolder(LayoutInflater inflater, ViewGroup parent) {
                    try {
                        return constructor.newInstance(inflater, parent);
                    } catch (Exception e1) {
                        throw new IllegalStateException(e1);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(holderClass.getSimpleName() + " must have a constructor with a LayoutInflater and a ViewGroup as parameters");
        }
    }

    public static <T> HolderFactory<T> create(@LayoutRes final int layoutId, Class<? extends RecyclerHolder<? extends T>> holderClass) {
        try {
            final Constructor<? extends RecyclerHolder<? extends T>> constructor = holderClass.getConstructor(View.class);
            return new HolderFactory<T>() {
                @Override
                public RecyclerHolder<? extends T> createHolder(LayoutInflater inflater, ViewGroup parent) {
                    try {
                        return constructor.newInstance(inflater.inflate(layoutId, parent, false));
                    } catch (Exception e1) {
                        throw new IllegalStateException(e1);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(holderClass.getSimpleName() + " must have a constructor with a View as parameters");
        }
    }

    public static <T> HolderFactory<T> create(@LayoutRes final int layoutId, @NotNull final HolderBinder<T> binder) {
        return new HolderFactory<T>() {
            @Override
            public RecyclerHolder<? extends T> createHolder(LayoutInflater inflater, ViewGroup parent) {
                return new RecyclerHolder<T>(inflater.inflate(layoutId, parent, false)) {
                    @Override
                    public void setContent(T content, int index) {
                        binder.bindHolder(itemView, content, index);
                    }
                };
            }
        };
    }

    public static abstract class SimpleHolderFactory<T> extends HolderFactory<T> {
        private final int layoutId;

        public SimpleHolderFactory(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
        }

        public abstract RecyclerHolder<? extends T> createHolder(View cellView);

        @Override
        public RecyclerHolder<? extends T> createHolder(LayoutInflater inflater, ViewGroup parent) {
            return createHolder(inflater.inflate(layoutId, parent, false));
        }
    }

}
