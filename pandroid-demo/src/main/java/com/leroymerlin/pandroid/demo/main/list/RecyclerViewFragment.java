package com.leroymerlin.pandroid.demo.main.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.ui.list.recyclerview.RecyclerFactory;
import com.leroymerlin.pandroid.ui.list.recyclerview.RecyclerHolder;
import com.leroymerlin.pandroid.ui.list.recyclerview.RecyclerViewAdapter;
import com.leroymerlin.pandroid.ui.list.recyclerview.SimpleItemTouchHelperCallback;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Created by florian on 17/11/14.
 */
public class RecyclerViewFragment extends ListFragment {


    @Inject
    ToastManager toastManager;

    @BindView(R.id.recycler_view_rv)
    protected RecyclerView rvMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //tag::RecyclerViewAdapter[]
        final RecyclerViewAdapter<String> adapter = new RecyclerViewAdapter<String>(new RecyclerFactory<RecyclerHolder<String>>() {
            @Override
            public RecyclerHolder<String> create(LayoutInflater inflater, ViewGroup parent, int viewType) {
                //override the adapter getItemType to handle different type of cell
                //here you can return the holder dealing with the object and type you have
                return new RecyclerHolder<String>(inflater.inflate(R.layout.cell_list, parent, false)) {

                    public TextView tvContent;

                    @Override
                    protected void bindView(View view) {
                        super.bindView(view);
                        tvContent = (TextView) view;
                    }

                    @Override
                    public void setContent(String content, int index) {
                        tvContent.setText(content);
                    }
                };
            }
        }
        );

        //this Touch Helper handle swipe to remove gesture and change position
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(adapter) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                toastManager.makeToast(getActivity(), "Item " + viewHolder.getAdapterPosition() + " removed", null, R.style.Toast_Warm);
                super.onSwiped(viewHolder, direction);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        touchHelper.attachToRecyclerView(rvMenu);
        adapter.addAll(getData());

        //you can add item click listener and long click listener on you adapter
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(RecyclerViewAdapter<String> parent, View view, int position, long id) {
                toastManager.makeToast(getActivity(), "Button " + position + " clicked", null, R.style.Toast, getResources().getInteger(R.integer.toast_short_duration));
            }
        });
        rvMenu.setAdapter(adapter);

        //end::RecyclerViewAdapter[]


        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        rvMenu.setLayoutManager(layout);

        rvMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.move(2, 6);
            }
        }, 1000);
    }


}
