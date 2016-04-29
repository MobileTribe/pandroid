package com.leroymerlin.pandroid.ui.list.listview;

public interface TypedFactory<T> extends Factory<TypedHolder<T>> {

    public boolean canCreate(Object object);

}
