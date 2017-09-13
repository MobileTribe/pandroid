package com.leroymerlin.pandroid.dagger;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.app.PandroidActivity;
import com.leroymerlin.pandroid.app.PandroidDialogFragment;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.opener.ActivityOpener;
import com.leroymerlin.pandroid.event.opener.FragmentEventReceiver;
import com.leroymerlin.pandroid.event.opener.FragmentOpener;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.leroymerlin.pandroid.event.opener.Opener;
import com.leroymerlin.pandroid.event.opener.OpenerEventReceiver;
import com.leroymerlin.pandroid.event.opener.OpenerReceiverProvider;
import com.leroymerlin.pandroid.log.LogWrapper;

import okhttp3.OkHttpClient;

/**
 * Created by mehdi on 03/12/2015.
 */
public interface BaseComponent {

    LogWrapper logWrapper();

    EventBusManager eventBusManager();

    OkHttpClient.Builder okHttpClientBuilder();

    void inject(OpenerEventReceiver<OpenerReceiverProvider, Opener> receiver);

    void inject(PandroidApplication pandroidApplication);

}
