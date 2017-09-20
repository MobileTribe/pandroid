package com.leroymerlin.pandroid.demo.main.opener;

import android.graphics.Color;
import android.os.Bundle;

import com.leroymerlin.pandroid.app.PandroidActivity;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.main.MainActivity;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.opener.FragmentEventReceiver;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * Example for opener usage
 * <p>
 * Created by florian on 27/07/2017.
 */

public class OpenerActivity extends PandroidActivity<CustomActivityOpener> {

    @Inject
    ToastManager toastManager;

    //tag::ActivityOpener[]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        //I can access my opener if the activity was open by an ActivityEventReceiver
        if (opener != null) {
            String myOpenerParam = opener.param;
            toastManager.makeToast(this, "param was : " + myOpenerParam, null);
        }
    }
    //end::ActivityOpener[]

    @OnClick(R.id.opener_home)
    public void onHomePressed() {
        startActivity(MainActivity.class);
    }


    @OnClick(R.id.opener_new_fragment)
    public void onNewFragmentPressed() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        sendEventSync(new ColorOpener(color));
    }


    @Override
    public List<EventBusManager.EventBusReceiver> getReceivers() {
        List<EventBusManager.EventBusReceiver> receivers = super.getReceivers();
        receivers.add(new FragmentEventReceiver()
                .setAnim(FragmentEventReceiver.ANIM_FADE)
                .setBackStackTag("tag")
                .setContainerId(R.id.opener_fragment_content)
                .addFragment(ColorFragment.class));
        return receivers;
    }
}
