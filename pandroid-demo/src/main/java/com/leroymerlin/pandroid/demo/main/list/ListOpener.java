package com.leroymerlin.pandroid.demo.main.list;

import com.leroymerlin.pandroid.event.opener.FragmentOpener;

/**
 * Created by florian on 23/12/14.
 */
public class ListOpener extends FragmentOpener {

    public final int lineParam;

    public ListOpener(Class<? extends ListFragment> mClass, int lineParam) {
        super(mClass);
        this.lineParam = lineParam;
    }

}
