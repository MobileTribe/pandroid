package com.leroymerlin.pandroid.demo.main.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.ui.list.QuickHeaderLayout;
import com.leroymerlin.pandroid.ui.list.listview.Factory;
import com.leroymerlin.pandroid.ui.list.listview.Holder;
import com.leroymerlin.pandroid.ui.list.listview.HolderAdapter;

import butterknife.BindView;


/**
 * Created by florian on 17/11/14.
 */
public class ListViewFragment extends ListFragment implements View.OnClickListener {


    @BindView(R.id.list_lv)
    protected ListView lvContent;

    @BindView(R.id.list_header_qhl)
    protected QuickHeaderLayout quickHeaderLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.list_header_btn_disable).setOnClickListener(this);
        view.findViewById(R.id.list_header_btn_enable).setOnClickListener(this);
        view.findViewById(R.id.list_header_btn_hide).setOnClickListener(this);
        view.findViewById(R.id.list_header_btn_show).setOnClickListener(this);
        lvContent.setAdapter(new HolderAdapter<String>(LayoutInflater.from(getActivity()), new Factory<Holder<String>>() {
            @Override
            public Holder<String> create() {
                return new Holder<String>() {
                    public TextView tvRoot;

                    @Override
                    public View getView(LayoutInflater inflater, ViewGroup parent, String content) {
                        tvRoot = (TextView) inflater.inflate(R.layout.cell_list, parent, false);
                        return tvRoot;
                    }

                    @Override
                    public void setContent(String content, int index) {
                        tvRoot.setText(content);
                    }
                };
            }
        }, getData()));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.list_header_btn_disable:
                quickHeaderLayout.setHeaderEnable(false);
                break;
            case R.id.list_header_btn_enable:
                quickHeaderLayout.setHeaderEnable(true);
                break;
            case R.id.list_header_btn_hide:
                quickHeaderLayout.setHeaderOpenValue(0.5f, true);
                break;
            case R.id.list_header_btn_show:
                quickHeaderLayout.setHeaderOpenValue(1, true);
                break;

        }
    }
}
