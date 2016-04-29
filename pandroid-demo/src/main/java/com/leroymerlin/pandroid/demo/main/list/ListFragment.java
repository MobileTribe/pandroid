package com.leroymerlin.pandroid.demo.main.list;

import com.leroymerlin.pandroid.app.PandroidFragment;
import java.util.ArrayList;


/**
 * Created by florian on 17/11/14.
 */
public class ListFragment extends PandroidFragment<ListOpener> {


    public ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < mOpener.lineParam; i++) {
            data.add("Button - " + i);
        }
        return data;
    }


}
