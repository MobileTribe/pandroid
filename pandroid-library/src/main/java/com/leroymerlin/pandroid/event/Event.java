package com.leroymerlin.pandroid.event;

import java.io.Serializable;

/**
 * Created by florian on 30/11/14.
 */
public abstract class Event implements Serializable {
    public abstract String getFilterTag();
}
