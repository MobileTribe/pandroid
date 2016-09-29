package com.leroymerlin.pandroid.demo.main.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.leroymerlin.pandroid.demo.BR;
import com.leroymerlin.pandroid.mvvm.ViewModelDelegate;

/**
 * Created by florian on 29/09/2016.
 */

public class MvvmViewModel extends ViewModelDelegate<MvvmViewModel.ExampleViewContract> {


    public ProductDataBinding productDataBinding = new ProductDataBinding();

    public MvvmViewModel() {
        //Injection if needed
    }

    @Override
    public void onCreateView(ExampleViewContract target, View view, Bundle savedInstanceState) {
        super.onCreateView(target, view, savedInstanceState);
        loadProduct();

        ViewDataBinding viewDataBinding = DataBindingUtil.bind(view);
        viewDataBinding.setVariable(BR.mvvmViewModel, this);
    }



    public void onSubmit(View view) {
        if (TextUtils.isEmpty(productDataBinding.name.get())) {
            productDataBinding.nameError.set("error");
            return;
        }

        productDataBinding.nameVisible.set(productDataBinding.nameVisible.get() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        contract.onSubmit(productDataBinding);
    }

    public void loadProduct() {
        productDataBinding.name.set("test");
        productDataBinding.nameVisible.set(View.VISIBLE);
    }


    interface ExampleViewContract {

        void onSubmit(ProductDataBinding productDataBinding);
    }
}
