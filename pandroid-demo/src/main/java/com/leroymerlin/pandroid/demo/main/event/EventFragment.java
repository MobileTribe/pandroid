package com.leroymerlin.pandroid.demo.main.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.event.EventBusManager;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.ui.toast.ToastManager;
import com.leroymerlin.pandroid.annotations.EventReceiver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by florian on 09/12/15.
 */
public class EventFragment extends PandroidFragment<FragmentOpener> {


    private static final String EVENT_TAG = "EVENT_TAG.Clicked";
    @Inject
    ToastManager toastManager;

    @BindView(R.id.event_text_to_send)
    TextView tvMessage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @OnClick(R.id.event_send_tag)
    public void sendTag() {
        eventBusManager.send(null, EVENT_TAG); // send a string object with no tag
    }

    //tag::sendSimpleEvent[]
    @OnClick(R.id.event_send_localy)
    public void sendLocaly() {
        eventBusManager.send(tvMessage.getText().toString()); // send a string object with no tag
    }
    //end::sendSimpleEvent[]

    //tag::sendTaggedEvent[]
    @OnClick(R.id.event_send_to)
    public void sendToSecondFragment() {
        eventBusManager.send(tvMessage.getText().toString(), EventSecondFragment.TAG, EventBusManager.DeliveryPolicy.AT_LEAST_ONE);
    }
    //end::sendTaggedEvent[]

    @OnClick(R.id.event_open_second_fragment)
    public void openSecondFragment() {
        startFragment(EventSecondFragment.class);
    }

    @EventReceiver(value = EVENT_TAG)
    public void receiveLocalTag() {
        toastManager.makeToast(getActivity(), "message with tag " + EVENT_TAG + " received", null);
    }

    //tag::sendSimpleEvent[]
    // generate a $CURRENT_CLASS$ReceiversProvider that refer this method
    // a Pandroid lifecycle delegate will register this generate class to the event bus for you
    // in non PandroidLifecycle class you will have to un/register the provider by yourself
    @EventReceiver
    public void receiveLocalMessage(String message) {
        toastManager.makeToast(getActivity(), message, null);
    }
    //end::sendSimpleEvent[]

}
