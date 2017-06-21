package com.leroymerlin.pandroid.demo.main.rx;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.demo.models.Product;
import com.leroymerlin.pandroid.future.ActionDelegate;

import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Created by florian on 20/06/2017.
 */

public interface ManagerWithDelegate {

    @RxWrapper
    void getProduct(String id, ActionDelegate<Product> actionDelegate);


    @RxWrapper(stream = true)
    void listenProductChange(ActionDelegate<Product> actionDelegate);


    @RxWrapper()
    String lastProductId();


}
