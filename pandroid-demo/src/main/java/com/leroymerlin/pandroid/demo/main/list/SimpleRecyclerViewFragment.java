package com.leroymerlin.pandroid.demo.main.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.ui.list.recyclerview.SimpleRecyclerViewAdapter;

import butterknife.BindView;


/**
 * Created by florian on 17/11/14.
 */
public class SimpleRecyclerViewFragment extends ListFragment {

    @BindView(R.id.recycler_view_rv)
    protected RecyclerView rvMenu;
    private SimpleRecyclerViewAdapter<String> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //tag::SimpleRecyclerViewAdapter[]
        adapter = new SimpleRecyclerViewAdapter<>(new SimpleRecyclerViewAdapter.SimpleHolder<String>(R.layout.cell_list) {
            @Override
            public void setContent(String content, View view, int index) {
                ((TextView) view).setText(content);
            }
        });
        adapter.addAll(getData());
        rvMenu.setAdapter(adapter);
        //end::SimpleRecyclerViewAdapter[]
        rvMenu.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


}
