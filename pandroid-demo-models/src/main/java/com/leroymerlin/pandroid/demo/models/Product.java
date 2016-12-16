package com.leroymerlin.pandroid.demo.models;

import com.leroymerlin.pandroid.annotations.DataBinding;

/**
 * Created by adrien on 28/10/2016.
 */
//tag::PandroidDataBinding[]
@DataBinding
public class Product {

    private String name;
    private int id;
    //end::PandroidDataBinding[]
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
