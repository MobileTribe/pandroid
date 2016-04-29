
package com.leroymerlin.pandroid.ui.list.listview;

public interface TypedHolder<T> extends Holder<T> {
    public boolean canHandle(Object object);
}
