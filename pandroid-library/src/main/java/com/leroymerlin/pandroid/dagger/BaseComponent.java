package com.leroymerlin.pandroid.dagger;

import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.app.PandroidActivity;
import com.leroymerlin.pandroid.app.PandroidDialogFragment;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.FragmentEventReceiver;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.event.ReceiversProvider;
import com.leroymerlin.pandroid.log.LogWrapper;

/**
 * Created by mehdi on 03/12/2015.
 */
public interface BaseComponent {

    LogWrapper logWrapper();

    EventBusManager eventBusManager();

    void inject(FragmentEventReceiver<ReceiversProvider> receiver);

    void inject(PandroidApplication pandroidApplication);

    void inject(PandroidActivity pandroidActivity);

    void inject(PandroidFragment<FragmentOpener> pandroidFragment);

    void inject(PandroidDialogFragment<FragmentOpener> pandroidFragment);

}
