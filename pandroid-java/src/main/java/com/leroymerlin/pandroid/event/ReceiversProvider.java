package com.leroymerlin.pandroid.event;

import java.util.List;

/**
 * Created by florian on 26/11/15.
 */
public interface ReceiversProvider {
    String SUFFIX_RECEIVER_PROVIDER = "ReceiversProvider";

    List<EventBusManager.EventBusReceiver> getReceivers();

}
