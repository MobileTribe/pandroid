package com.leroymerlin.pandroid.demo.models;

import com.pandroid.annotations.DataBinding;

import java.util.List;

/**
 * Created by adrien on 28/10/2016.
 */
@DataBinding
public class Product {

    private String name;
    private int id;

    //  private List<String> list;


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

   /* public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }*/
}
