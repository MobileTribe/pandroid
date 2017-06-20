package com.leroymerlin.pandroid.demo.main.rx;

import com.leroymerlin.pandroid.annotations.RxWrapper;
import com.leroymerlin.pandroid.demo.models.Product;
import com.leroymerlin.pandroid.future.ActionDelegate;

/**
 * Created by florian on 20/06/2017.
 */

public interface ManagerWithDelegate {

    @RxWrapper
    void singleMethodWithDelegate(String id, ActionDelegate<Product> actionDelegate);


    @RxWrapper(single = false)
    void observableMethodWithDelegate(ActionDelegate<Product> actionDelegate);



    String simpleMethod();


}
