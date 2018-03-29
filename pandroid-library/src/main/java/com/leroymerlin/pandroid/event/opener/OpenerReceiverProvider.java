package com.leroymerlin.pandroid.event.opener;


import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.leroymerlin.pandroid.event.ReceiversProvider;


/**
 * Created by florian on 26/11/15.
 */
public interface OpenerReceiverProvider extends ReceiversProvider {

    FragmentManager provideFragmentManager();

    Activity provideActivity();

}
