package com.leroymerlin.pandroid.ui.list.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultiCategoryHolderAdapter<T> extends BaseAdapter {

    protected final LayoutInflater inflater;

    protected final List<TypedFactory<T>> factories = new ArrayList<TypedFactory<T>>();

    protected List<T> content = new ArrayList<T>();
    protected boolean recycleHolder = true;

    public MultiCategoryHolderAdapter(LayoutInflater inflater) {
        super();
        this.inflater = inflater;
    }

    public MultiCategoryHolderAdapter(LayoutInflater inflater, Collection<TypedFactory<T>> factories) {
        this(inflater);
        this.factories.addAll(factories);
    }

    public MultiCategoryHolderAdapter(LayoutInflater inflater, Collection<TypedFactory<T>> factories,
                                      Collection<T> content, boolean recycleHolder) {
        this(inflater, factories);
        this.recycleHolder = recycleHolder;
        addAll(content);
    }

    public void addFactory(TypedFactory<T> factory) {
        factories.add(factory);
    }

    public void removeFactory(TypedFactory<T> factory){
        factories.remove(factory);
    }

    public void add(T object) {
        content.add(object);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        content.remove(index);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends T> collection) {
        content.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return content.size();
    }

    public void clear() {
        content.clear();
        notifyDataSetChanged();
    }

    @Override
    public final Object getItem(int position) {
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return factories.indexOf(getFactoryFor(content.get(position)));
    }

    public final T getItemAt(int position) {
        if (position < content.size()) {
            return content.get(position);
        }
        return null;

    }

    protected TypedFactory<T> getFactoryFor(T obj) {
        for (TypedFactory<T> typedFactory : factories) {
            if (typedFactory.canCreate(obj)) {
                return typedFactory;
            }
        }
        throw new IllegalArgumentException("No factory found to display " + obj.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = getItemAt(position);

        if (!recycleHolder || convertView == null || !((TypedHolder<T>) convertView.getTag()).canHandle(item)) {
            TypedHolder<T> holder = getFactoryFor(item).create();
            convertView = holder.getView(inflater, parent, item);
            convertView.setTag(holder);
        }
        com.leroymerlin.pandroid.ui.list.listview.TypedHolder<T> holder = (TypedHolder<T>) convertView.getTag();
        holder.setContent(item, position);
        return convertView;
    }
}
