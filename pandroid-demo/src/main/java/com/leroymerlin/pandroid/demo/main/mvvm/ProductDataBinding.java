package com.leroymerlin.pandroid.demo.main.mvvm;

import android.databinding.ObservableInt;

import com.leroymerlin.pandroid.mvvm.BindableString;
import com.leroymerlin.pandroid.mvvm.ViewModel;

/**
 * Created by adrien on 14/09/16.
 */
public class ProductDataBinding implements ViewModel {

    public BindableString name = new BindableString();
    public ObservableInt nameVisible = new ObservableInt();
    public BindableString nameError = new BindableString();


}
