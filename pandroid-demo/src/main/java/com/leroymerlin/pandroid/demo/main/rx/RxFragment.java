package com.leroymerlin.pandroid.demo.main.rx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.models.Product;
import com.leroymerlin.pandroid.event.FragmentOpener;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * Created by florian on 20/06/2017.
 */

public class RxFragment extends PandroidFragment<FragmentOpener> {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rx, container, false);
    }

}
