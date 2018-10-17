package com.leroymerlin.pandroid.ui.list.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by florian on 19/11/14.
 */
public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerHolder<? extends T>> implements View.OnClickListener, View.OnLongClickListener, RecyclerView.OnChildAttachStateChangeListener {

    protected static final int VIEW_HOLDER_TAG = R.id.pandroid_holder_tag;

    protected RecyclerFactory<? extends RecyclerHolder<? extends T>> factory;
    protected RecyclerView mRecyclerView;
    protected List<T> content = new ArrayList<T>();

    private OnItemClickListener<T> itemClickListener;
    private OnLongClickListener<T> itemLongClickListener;

    public RecyclerViewAdapter() {
        super();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        recyclerView.addOnChildAttachStateChangeListener(this);

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.mRecyclerView = null;
        recyclerView.removeOnChildAttachStateChangeListener(this);
    }

    public RecyclerViewAdapter(RecyclerFactory<? extends RecyclerHolder<? extends T>> factory) {
        super();
        setFactory(factory);
    }

    public RecyclerViewAdapter(RecyclerFactory<? extends RecyclerHolder<? extends T>> factory, List<T> content) {
        this(factory);
        addAll(content);
    }

    public void setFactory(RecyclerFactory<? extends RecyclerHolder<? extends T>> factory) {
        this.factory = factory;
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

    public void addDiff(final @NotNull List<? extends T> collection) {
        final List<T> oldCollection = Collections.unmodifiableList(content);
        DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldCollection.size();
            }

            @Override
            public int getNewListSize() {
                return collection.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldCollection.get(oldItemPosition).hashCode() == collection.get(newItemPosition).hashCode();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldCollection.get(oldItemPosition).equals(collection.get(newItemPosition));
            }
        };
        this.addDiff(collection, diffCallback, true);
    }

    public void addDiff(@NotNull List<? extends T> collection, @NotNull DiffUtil.Callback callback, boolean detectMove) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback, detectMove);
        content.clear();
        content.addAll(collection);
        diffResult.dispatchUpdatesTo(this);
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


    @NonNull
    @Override
    public RecyclerHolder<? extends T> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (factory == null) {
            throw new IllegalStateException("Holder factory can't be null");
        }
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        RecyclerHolder<? extends T> tRecyclerHolder = factory.create(layoutInflater, viewGroup, i);
        tRecyclerHolder.itemView.setTag(VIEW_HOLDER_TAG, tRecyclerHolder);
        return tRecyclerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder<? extends T> tRecyclerHolder, int i) {
        ((RecyclerHolder) tRecyclerHolder).setContent(getItemAt(i), i);
    }


    @Override
    public int getItemCount() {
        return content.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerHolder tRecyclerHolder = (RecyclerHolder) v.getTag(VIEW_HOLDER_TAG);
        int position = tRecyclerHolder.getAdapterPosition();
        if (itemClickListener != null)
            itemClickListener.onItemClick(this, v, position, tRecyclerHolder.getItemId());
    }


    @Override
    public void onChildViewAttachedToWindow(View view) {
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        view.setOnClickListener(null);
        view.setOnLongClickListener(null);
    }


    @Override
    public boolean onLongClick(View view) {
        RecyclerHolder tRecyclerHolder = (RecyclerHolder) view.getTag(VIEW_HOLDER_TAG);
        int position = tRecyclerHolder.getAdapterPosition();
        if (itemLongClickListener != null) {
            itemLongClickListener.onItemLongClick(this, view, position, tRecyclerHolder.getItemId());
            return true;
        }

        return false;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnLongClickListener<T> listener) {
        this.itemLongClickListener = listener;
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