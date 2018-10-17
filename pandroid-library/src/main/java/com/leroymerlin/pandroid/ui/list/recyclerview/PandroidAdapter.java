package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by florian on 19/11/14.
 */
public class PandroidAdapter<T> extends RecyclerViewAdapter<T> {

    private ItemTypeMatcher<T> itemTypeMatcher = new HashCodeItemTypeMatcher<>();

    public PandroidAdapter(@Nullable SparseArray<HolderFactory<T>> holderFactories) {
        super();
        factory = (RecyclerFactory<? extends RecyclerHolder<? extends T>>) new SimpleFactory<>();
        if (holderFactories != null) {
            for (int i = 0; i < holderFactories.size(); i++) {
                int itemType = holderFactories.keyAt(i);
                registerFactory(itemType, holderFactories.get(itemType));
            }
        }
    }

    public PandroidAdapter() {
        this(null);
    }

    SimpleFactory<T> getFactory() {
        return (SimpleFactory<T>) factory;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemTypeMatcher != null) {
            return itemTypeMatcher.getItemViewType(this, getItemAt(position), position);
        }
        return super.getItemViewType(position);

    }

    public void setItemTypeMatcher(ItemTypeMatcher<T> typeMatcher) {
        this.itemTypeMatcher = typeMatcher;
    }

    public void registerFactory(Class itemClass, HolderFactory<T> factory) {
        registerFactory(itemClass.hashCode(), factory);
    }

    public void registerFactory(int itemType, HolderFactory<T> factory) {
        getFactory().holderFactories.put(itemType, factory);
    }

    public void unregisterFactory(Class itemClass) {
        unregisterFactory(itemClass.hashCode());
    }

    public void unregisterFactory(int itemType) {
        getFactory().holderFactories.remove(itemType);
    }

    static class SimpleFactory<T> implements RecyclerFactory<RecyclerHolder<? extends T>> {

        SparseArray<HolderFactory<T>> holderFactories = new SparseArray<>();

        @Override
        public RecyclerHolder<? extends T> create(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return holderFactories.get(viewType).createHolder(inflater, parent);
        }
    }

    private static class HashCodeItemTypeMatcher<T> implements ItemTypeMatcher<T> {
        @Override
        public int getItemViewType(PandroidAdapter<T> adapter, T item, int position) {
            if (adapter.getFactory().holderFactories.size() > 1 && item != null) {
                return item.getClass().hashCode();
            } else if (adapter.getFactory().holderFactories.size() == 1) {
                return adapter.getFactory().holderFactories.keyAt(0);
            } else {
                return 0;
            }
        }
    }

    public interface ItemTypeMatcher<T> {
        int getItemViewType(PandroidAdapter<T> adapter, T item, int position);
    }
}