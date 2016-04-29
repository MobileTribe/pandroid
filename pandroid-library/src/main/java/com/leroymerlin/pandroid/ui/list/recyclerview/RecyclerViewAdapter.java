package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by florian on 19/11/14.
 */
public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerHolder<T>> implements View.OnClickListener, View.OnLongClickListener {

    private final RecyclerFactory<? extends RecyclerHolder<T>> factory;
    private List<T> content = new ArrayList<T>();
    private OnItemClickListener itemClickListener;
    private OnLongClickListener itemLongClickListener;

    public RecyclerViewAdapter(RecyclerFactory<? extends RecyclerHolder<T>> factory) {
        super();
        this.factory = factory;
    }

    public RecyclerViewAdapter(RecyclerFactory<? extends RecyclerHolder<T>> factory, List<T> content) {
        this(factory);
        addAll(content);
    }


    public void add(T object) {
        content.add(object);
        notifyItemInserted(content.size() - 1);
    }

    public void add(int index, T object) {
        content.add(index, object);
        notifyItemInserted(index);
    }

    public void move(int fromPosition, int toPosition) {
        T data = content.remove(fromPosition);
        content.add(toPosition, data);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void remove(int index) {
        content.remove(index);
        notifyItemRemoved(index);
    }

    public void remove(Collection<? extends T> collection) {
        for (T object : collection) {
            int index = content.indexOf(object);
            if (index >= 0) {
                remove(index);
            }
        }
    }

    public void update(int index, T object) {
        content.remove(index);
        content.add(index, object);
        notifyItemChanged(index);
    }

    public void update(Collection<? extends T> collection, boolean removeOld) {
        if (removeOld) {
            for (int i = content.size() - 1; i >= 0; i--) {
                if (!collection.contains(content.get(i))) {
                    remove(i);
                }
            }

        }
        int i = 0;
        for (T object : collection) {
            if (content.contains(object)) {
                int index = content.indexOf(object);
                if (i == index || !removeOld) {
                    update(index, object);
                } else {
                    move(index, i);
                }
            } else {
                if (removeOld)
                    add(i, object);
                else
                    add(object);
            }
            i++;
        }

    }

    public Collection<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    /**
     * you have to modify the change to the adapter if you modify this list
     *
     * @return list of content
     */
    public List getContentList() {
        return content;
    }

    public void addAll(Collection<? extends T> collection) {
        int index = content.size();
        content.addAll(index, collection);
        notifyItemRangeInserted(index, collection.size());
    }

    public void addAll(int index, Collection<? extends T> collection) {
        content.addAll(index, collection);
        notifyItemRangeInserted(index, collection.size());
    }

    public void clear() {
        content.clear();
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItemAt(int position) {
        return content.get(position);

    }


    @Override
    public RecyclerHolder<T> onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerHolder<T> tRecyclerHolder = factory.create(LayoutInflater.from(viewGroup.getContext()), viewGroup, i);
        tRecyclerHolder.itemView.setOnClickListener(this);
        tRecyclerHolder.itemView.setOnLongClickListener(this);
        tRecyclerHolder.itemView.setTag(tRecyclerHolder);
        return tRecyclerHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerHolder<T> tRecyclerHolder, int i) {
        tRecyclerHolder.setContent(getItemAt(i), i);

    }


    @Override
    public int getItemCount() {
        return content.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerHolder<T> tRecyclerHolder = (RecyclerHolder<T>) v.getTag();
        int position = tRecyclerHolder.getPosition();
        if (itemClickListener != null)
            itemClickListener.onItemClick(this, v, position, tRecyclerHolder.getItemId());
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        void onItemClick(RecyclerViewAdapter<T> parent, View view, int position, long id);
    }

    @Override
    public boolean onLongClick(View view) {
        RecyclerHolder<T> tRecyclerHolder = (RecyclerHolder<T>) view.getTag();
        int position = tRecyclerHolder.getPosition();
        if (itemLongClickListener != null) {
            itemLongClickListener.onItemLongClick(this, view, position, tRecyclerHolder.getItemId());
            return true;
        }

        return false;
    }

    public void setOnItemLongClickListener(OnLongClickListener<T> listener) {
        this.itemLongClickListener = listener;
    }

    public interface OnLongClickListener<T> {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        void onItemLongClick(RecyclerViewAdapter<T> parent, View view, int position, long id);
    }

}