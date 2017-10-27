package com.leroymerlin.pandroid.templates.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.annotations.BindLifeCycleDelegate;
import com.leroymerlin.pandroid.app.PandroidActivity;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.app.ResumeState;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.opener.FragmentEventReceiver;

import java.util.List;

public class TemplateActivity extends PandroidActivity<TemplateActivityOpener> implements TemplateActivityPresenter.PresenterView {

    @BindLifeCycleDelegate
    TemplateActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onResume(ResumeState state) {
        super.onResume(state);
        switch (state) {
            case FIRST_START:
                //startFragment();//TODO add fragment
                break;
        }
    }

    @Override
    public Fragment getCurrentFragment() {
        return getFragmentManager().findFragmentById(R.id.main_content_container); //useful to handle onBackListener in fragment
    }


    @Override
    public List<EventBusManager.EventBusReceiver> getReceivers() {
        List<EventBusManager.EventBusReceiver> receivers = super.getReceivers();
        receivers.add(
                new FragmentEventReceiver()
                        .setContainerId(R.id.main_content_container)
                        .setAnim(FragmentEventReceiver.ANIM_FADE)
                //.setBackStackTag("myBackTag")
                //.addFragment() //TODO add fragment to open

        );
        return receivers;
    }
}
