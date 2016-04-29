package com.leroymerlin.pandroid.ui.list.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HolderAdapter<T> extends BaseAdapter {

    protected final LayoutInflater inflater;
    protected final Factory<? extends Holder<T>> factory;
    protected List<T> content = new ArrayList<T>();
    protected final boolean recycleHolder;


    public HolderAdapter(LayoutInflater inflater, com.leroymerlin.pandroid.ui.list.listview.Factory<? extends com.leroymerlin.pandroid.ui.list.listview.Holder<T>> factory) {
        this(inflater, factory, Collections.<T>emptyList());
    }

    public HolderAdapter(LayoutInflater inflater, Factory<? extends Holder<T>> factory,
                         Collection<T> content) {
        this(inflater, factory, content, true);

    }

    public HolderAdapter(LayoutInflater inflater, com.leroymerlin.pandroid.ui.list.listview.Factory<? extends Holder<T>> factory,
                         Collection<T> content, boolean recycleHolder) {
        super();
        this.inflater = inflater;
        this.factory = factory;
        this.recycleHolder = recycleHolder;
        addAll(content);
    }


    public void add(T object) {
        content.add(object);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        content.remove(index);
        notifyDataSetChanged();
    }

    public Collection<T> getContent() {
        return Collections.unmodifiableList(content);
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

    public final T getItemAt(int position) {
        if (position < content.size()) {
            return content.get(position);
        }
        return null;

    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = getItemAt(position);
        if (!recycleHolder || convertView == null) {
            Holder<T> holder = factory.create();
            convertView = holder.getView(inflater, parent, item);
            convertView.setTag(holder);
        }
        Holder<T> holder = (com.leroymerlin.pandroid.ui.list.listview.Holder<T>) convertView.getTag();
        holder.setContent(item, position);
        return convertView;
    }
}
