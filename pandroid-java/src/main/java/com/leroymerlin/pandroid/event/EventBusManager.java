package com.leroymerlin.pandroid.event;

import java.util.List;

/**
 * Created by florian on 26/11/15.
 */
public interface EventBusManager {

    String send(Object data);

    String sendTag(String tag);

    String send(Object data, String messageTag);

    String send(Object data, String messageTag, DeliveryPolicy deliveryPolicy);

    String send(Object data, String messageTag, DeliveryPolicy deliveryPolicy, String messageID);

    String sendSync(Object data);

    String sendTagSync(String tag);

    String sendSync(Object data, String messageTag);

    String sendSync(Object data, String messageTag, DeliveryPolicy deliveryPolicy);

    String sendSync(Object data, String messageTag, DeliveryPolicy deliveryPolicy, String messageID);

    boolean cancel(String id);

    boolean clearEventBox();

    void registerReceiver(EventBusReceiver eventBusReceiver);

    void registerReceivers(List<EventBusReceiver> eventBusReceivers);

    void unregisterReceiver(EventBusReceiver eventBusReceiver);

    void unregisterReceivers(List<EventBusReceiver> eventBusReceivers);

    interface EventBusReceiver {
        List<String> getTags();

        boolean handle(String tag, Object data);
    }

    //tag::deliveryPolicy[]
    enum DeliveryPolicy {
        //The message will be delivery only to the registered receiver at the moment the message is send
        UNCHECKED,
        //The message will be keep until at least one receiver get it
        AT_LEAST_ONE,
        //The message will be keep until another message with the same id override it or it is cancelled
        UNLIMITED
    }
    //end::deliveryPolicy[]

}
