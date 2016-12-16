package com.leroymerlin.pandroid.demo.main.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.annotations.EventReceiver;

import butterknife.BindView;

/**
 * Created by florian on 18/01/16.
 */
public class EventSecondFragment extends PandroidFragment {

    public static final String TAG = "EventSecondFragment";

    @BindView(R.id.event_second_tv_message)
    TextView tvMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_second, container, false);
    }


    //tag::sendTaggedEvent[]
    @EventReceiver({TAG, "toto"}) //Generate provider with a tag to filter event
    public void onReceiveMessage(String msg) {
        String txt = tvMessage.getText().toString();
        String text = txt + "\n" + msg;
        logWrapper.d(TAG, "message receive: " + msg);
        tvMessage.setText(text);
    }
    //end::sendTaggedEvent[]

}
