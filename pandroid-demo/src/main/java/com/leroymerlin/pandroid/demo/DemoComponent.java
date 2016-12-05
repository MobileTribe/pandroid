package com.leroymerlin.pandroid.demo;

import com.leroymerlin.pandroid.analytics.AnalyticsManager;
import com.leroymerlin.pandroid.dagger.BaseComponent;
import com.leroymerlin.pandroid.dagger.PandroidModule;
import com.leroymerlin.pandroid.demo.globals.review.ReviewManager;
import com.leroymerlin.pandroid.demo.main.anim.AnimationFragment;
import com.leroymerlin.pandroid.demo.main.event.EventFragment;
import com.leroymerlin.pandroid.demo.main.list.ListViewFragment;
import com.leroymerlin.pandroid.demo.main.list.RecyclerViewFragment;
import com.leroymerlin.pandroid.demo.main.mvp.PresenterFragment;
import com.leroymerlin.pandroid.demo.main.mvvm.MvvmFragment;
import com.leroymerlin.pandroid.demo.main.rest.RestFragment;
import com.leroymerlin.pandroid.demo.main.scanner.ScannerFragment;
import com.leroymerlin.pandroid.demo.main.toast.ToastFragment;
import com.leroymerlin.pandroid.ui.picture.PictureManager;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by florian on 04/01/16.
 */
@Component(
        modules = {
                PandroidModule.class,
                DemoModule.class
        }
)

@Singleton
public interface DemoComponent extends BaseComponent {

    AnalyticsManager analytycsManager();

    ToastManager toastManager();

    ReviewManager reviewManager();

    PictureManager pictureManager();

    void inject(MvvmFragment mvvmFragment);

    void inject(ScannerFragment scannerFragment);

    void inject(AnimationFragment animationFragment);

    void inject(EventFragment eventFragment);

    void inject(ListViewFragment listViewFragment);

    void inject(RecyclerViewFragment recyclerViewFragment);

    void inject(RestFragment restFragment);

    void inject(ToastFragment restFragment);

    void inject(PresenterFragment fragment);

}