package com.leroymerlin.pandroid.demo.main.anim;

import com.leroymerlin.pandroid.event.opener.FragmentOpener;
import com.leroymerlin.pandroid.ui.animation.ViewInfosContainer;

/**
 * Created by florian on 20/10/2015.
 */
//tag::FragmentWithOpener[]
public class MaterialOpener extends FragmentOpener {

    //this is the params the fragment needs
    public ViewInfosContainer tvInfos;
    public ViewInfosContainer ivInfos;

    public MaterialOpener(ViewInfosContainer ivInfos, ViewInfosContainer tvInfos) {
        super(MaterialFragment.class); // this opener will instantiate a MaterialFragment
        this.ivInfos = ivInfos;
        this.tvInfos = tvInfos;
    }

}
//end::FragmentWithOpener[]
