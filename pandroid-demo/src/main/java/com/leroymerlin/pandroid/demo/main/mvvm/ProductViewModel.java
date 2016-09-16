package com.leroymerlin.pandroid.demo.main.mvvm;

import android.databinding.ObservableInt;
import android.text.TextUtils;
import android.view.View;

import com.leroymerlin.pandroid.mvvm.BindableString;
import com.leroymerlin.pandroid.mvvm.ViewModel;

/**
 * Created by adrien on 14/09/16.
 */
public class ProductViewModel implements ViewModel {

    public BindableString name = new BindableString();
    public ObservableInt nameVisible = new ObservableInt();
    public BindableString nameError = new BindableString();
    public ProductViewModelContract productViewModelContract;

    public ProductViewModel(ProductViewModelContract productViewModelContract) {
        this.productViewModelContract = productViewModelContract;
    }

    public void onSubmit(View view) {
        if (TextUtils.isEmpty(name.get())) {
            nameError.set("error");
            return;
        }

        nameVisible.set(nameVisible.get() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        productViewModelContract.onSubmit(view);
    }

    public void loadProduct() {
        name.set("test");
        nameVisible.set(View.VISIBLE);
    }

}
