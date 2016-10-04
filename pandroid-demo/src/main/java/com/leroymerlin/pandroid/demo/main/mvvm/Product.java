package com.leroymerlin.pandroid.demo.main.mvvm;

import com.pandroid.annotations.DataBinding;

import java.util.Date;

/**
 * Created by florian on 29/09/2016.
 */

@DataBinding
public class Product {

    private String name;
    public int intValue;
    private double doubleValue;
    private char charValue;
    private boolean booleanValue;

    private Date dateValue;

    public char getCharValue() {
        return charValue;
    }

    public void setCharValue(char charValue) {
        this.charValue = charValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
}

