package com.leroymerlin.pandroid.event.opener;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.leroymerlin.pandroid.event.ReceiversProvider;

import java.util.List;

/**
 * Created by florian on 26/11/15.
 */
public interface OpenerReceiverProvider extends ReceiversProvider {

    FragmentManager provideFragmentManager();

    Activity provideActivity();

}
