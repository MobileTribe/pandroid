package com.leroymerlin.pandroid.ui.list.recyclerview;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by florian on 10/02/16.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final RecyclerViewAdapter mAdapter;
    public SimpleItemTouchHelperCallback(RecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.remove(viewHolder.getAdapterPosition());
    }
}