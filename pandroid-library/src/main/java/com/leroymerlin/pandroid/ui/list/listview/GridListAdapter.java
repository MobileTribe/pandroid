package com.leroymerlin.pandroid.ui.list.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by paillardf on 26/03/2014.
 * <p/>
 * come from https://github.com/plattysoft/grid-with-header-list-adapter/blob/master/ListAsGridAdapter/src/com/plattysoft/ui/ListAsGridBaseAdapter.java
 */
public class GridListAdapter extends MultiCategoryHolderAdapter<Object> {



    private ArrayList<Row> rows = new ArrayList<Row>();

    /**
     * Number of column in row in the ListView.
     */
    private int mNumColumns;
    /**
     * Listener to handle click on a item like a {@link android.widget.GridView}.
     */
    private GridItemClickListener mGridItemClickListener;
    private int cellMargin;


    public GridListAdapter(final LayoutInflater inflater, int numColumns){
        super(inflater);
        setNumColumns(numColumns);
    }

    public GridListAdapter( LayoutInflater inflater, int numColumns,
                            Collection<TypedFactory<Object>> factories) {
        super(inflater, factories);
        setNumColumns(numColumns);
    }
    public GridListAdapter( LayoutInflater inflater, int numColumns,
                            Collection<TypedFactory<Object>>factories, Collection<Object> content, boolean recycled) {
        super(inflater, factories, content, true);
        setNumColumns(numColumns);
    }

    public void setCellMargin(int cellMargin) {
        this.cellMargin = cellMargin;
    }

    @Override
    @Deprecated
    public final int getCount() {
        return rows.size();
    }

    /**
     * @return number of cell in the gridview
     */
    public final int getCellCount() {
        return super.getCount();
    }

    @Deprecated
    @Override
    public void add(Object object) {
        super.add(object);
        convertToRow();
    }

    @Deprecated
    @Override
    public void remove(int index) {
        super.remove(index);
        convertToRow();
    }

    /**
     *
     * @param object to add
     * @param colspan
     */
    public void addCell(Object object, int colspan) {
        super.add(object);
        if(rows.isEmpty()||isRowFull(rows.get(rows.size() - 1), colspan)){
            Row row = new Row();
            row.indexFirstItem = content.size()-1;
            rows.add(row);
        }
        rows.get(rows.size()-1).add(new Cell(object, colspan));

    }

    /**
     *
     * @param objects to add
     * @param colspan for each object
     */
    public void addCells(List<? extends Object> objects, int colspan) {
        for(Object o : objects) {
            addCell(o,colspan);
        }
    }


    /**
     * Add all the object with a colspan of 1
     * Remove all previous colspan register
     *
     * Should never be used with addCell.
     *
     * @param collection
     */
    @Override
    public void addAll(Collection<? extends Object> collection) {
        super.addAll(collection);
        convertToRow();
    }

    @Override
    public void clear() {
        super.clear();
        convertToRow();
    }


    private void convertToRow() {
        rows.clear();
        if (content.isEmpty())
            return;

        Row r = new Row();
        Object lastAdded = null;
        for (Object o : content) {
            Class<? extends Object> oClass = ((Object) o).getClass();
            if (r.size() >= getNumColumns() || (lastAdded != null && (!oClass.isInstance(lastAdded)))) {
                rows.add(r);
                r = new Row();
            }
            lastAdded = o;
            r.add(new Cell(o, 1));
        }
        rows.add(r);
        updateRowPosition();
    }

    private void updateRowPosition() {
        int position = 0;
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).indexFirstItem = position;
            position += rows.get(i).size();
        }
    }

    @Override
    public final View getView(int position, View view, ViewGroup viewGroup) {
        // If it is possible, we re-use the current view of the list and we make the current line
        // be rows of the number of columns
        LinearLayout layout;
        if (view == null) {
            layout = createItemRow(position, viewGroup);
        } else {
            layout = (LinearLayout) view;
            updateItemRow(position, viewGroup, layout);
        }
        layout.setWeightSum(getNumColumns());
        return layout;
    }

    /**
     * Create some column in a new row.
     *
     * @param position  Current position of the cell.
     * @param viewGroup Container of the cell.
     * @return Layout of the row.
     */
    private LinearLayout createItemRow(int position, ViewGroup viewGroup) {
        final LinearLayout layout = new LinearLayout(inflater.getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        // Now add the sub views to it
        for (int i = 0; i < rows.get(position).size(); i++) {
            int currentPos = rows.get(position).indexFirstItem + i;
            // Get the new View
            View insideView;
            insideView = super.getView(currentPos, null, viewGroup);
            insideView.setVisibility(View.VISIBLE);

            layout.addView(insideView);
            insideView.setOnClickListener(new ListItemClickListener(currentPos));
            // Set the width of this column

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = rows.get(position).get(i).colSpan;
            if (i != mNumColumns - 1)// last col
                params.setMargins(0, 0, cellMargin, 0);
            insideView.setLayoutParams(params);

        }
        return layout;
    }

    /**
     * Update a existing view with some column.
     *
     * @param position  Current position of the cell.
     * @param viewGroup Container of the cell.
     * @param layout    Layout saved of the cell.
     */
    private void updateItemRow(int position, ViewGroup viewGroup, LinearLayout layout) {
        // we remove view that aren't needed for the row
        while(layout.getChildCount()>rows.get(position).size()){
            layout.removeViewAt(layout.getChildCount()-1);
        }

        for (int i = 0; i < rows.get(position).size(); i++) {
            int currentPos = rows.get(position).indexFirstItem + i;
            View insideView = layout.getChildAt(i);

            View theView = super.getView(currentPos, insideView, viewGroup);

            // Set the width of this column
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = rows.get(position).get(i).colSpan;
            if (i != mNumColumns - 1)// last col
                params.setMargins(0, 0, cellMargin, 0);
            theView.setLayoutParams(params);

            theView.setOnClickListener(new ListItemClickListener(currentPos));
            if (!theView.equals(insideView)) {
                if (layout.getChildCount() > i) {
                    layout.removeViewAt(i);
                }
                layout.addView(theView, i);
            }
        }
    }


    private void onGridItemClicked(View v, int position) {
        if (mGridItemClickListener != null) {
            mGridItemClickListener.onGridItemClicked(v, position, getItemId(position));
        }
    }


    public final int getNumColumns() {
        return mNumColumns;
    }

    public final void setNumColumns(int numColumns) {
        mNumColumns = Math.max(1, numColumns);
        notifyDataSetChanged();
    }

    public final void setOnGridClickListener(GridItemClickListener listener) {
        mGridItemClickListener = listener;
    }



    private boolean isRowFull(Row r, int colspan){
        int count = 0;
        for (Cell c : r){
            count += c.colSpan;
        }
        return (count+colspan)>getNumColumns();
    }

    private class ListItemClickListener implements View.OnClickListener {

        private int mPosition;

        public ListItemClickListener(int currentPos) {
            mPosition = currentPos;
        }

        @Override
        public void onClick(View v) {
            onGridItemClicked(v, mPosition);
        }
    }

    private class Row extends ArrayList<Cell> {
        /**
         * position of the first object in content
         */
        public int indexFirstItem;
    }

    private class Cell {

        Cell(Object data, int colSpan) {
            this.data = data;
            this.colSpan = colSpan;
        }

        Object data;
        int colSpan = 1;
    }


}
