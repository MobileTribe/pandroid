package com.leroymerlin.pandroid.demo.main.mvvm;

import android.view.View;

import com.leroymerlin.pandroid.mvvm.ViewModelContract;

/**
 * Created by adrien on 14/09/16.
 */
public interface ProductViewModelContract extends ViewModelContract {

    void onSubmit(View view);

}
