package com.leroymerlin.pandroid.demo.main.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.ui.list.recyclerview.HolderFactory;
import com.leroymerlin.pandroid.ui.list.recyclerview.PandroidAdapter;

import java.util.Random;

import butterknife.BindView;
import io.reactivex.Observable;


/**
 * Created by florian on 17/11/14.
 */
public class SimpleRecyclerViewFragment extends ListFragment {

    @BindView(R.id.recycler_view_rv)
    protected RecyclerView rvMenu;
    private PandroidAdapter<String> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //tag::PandroidAdapter[]
        adapter = new PandroidAdapter<>();
        adapter.registerFactory(0, HolderFactory.<String>create(R.layout.cell_list, (cellView, data, index) -> ((TextView) cellView).setText(data)));
        adapter.addAll(getData());
        adapter.setOnItemClickListener((parent, itemView, position, id) -> {
            changeData(position);
        });
        rvMenu.setAdapter(adapter);
        //end::PandroidAdapter[]


        rvMenu.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void changeData(int position) {
        Random random = new Random();
        adapter.addDiff(Observable.fromIterable(getData())
                .filter(item -> random.nextBoolean())
                .toList().blockingGet());
    }


}
