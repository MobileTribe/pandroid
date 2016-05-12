package com.leroymerlin.pandroid.event;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by florian on 26/11/15.
 */
public class EventBusManagerImpl implements EventBusManager {

    private static EventBusManagerImpl instance;

    private final Handler handler;

    private List<EventBusReceiver> receivers = new ArrayList<>();

    private final List<Message> box = new ArrayList<>();


    public EventBusManagerImpl() {
        handler = new Handler();
    }

    public static EventBusManagerImpl getMainInstance() {
        if (instance == null)
            instance = new EventBusManagerImpl();
        return instance;
    }

    private void checkBox() {
        synchronized (box) {
            for (int i = box.size() - 1; i >= 0; i--) {
                deliverMessage(box.get(i));
                if (box.get(i).isDelivered())
                    box.remove(i);
            }
        }
    }


    private void deliverMessage(Message message) {

        for (int i = receivers.size() - 1; i >= 0; i--) {
            EventBusReceiver receiver = receivers.get(i);
            if (message.messageTag == null || (receiver.getTags() != null && receiver.getTags().contains(message.messageTag))) {
                if (receiver.handle(message.data)) {
                    message.setDelivered();
                }
            }
        }
    }

    @Override
    public String send(Object data) {
        return send(data, null);
    }

    @Override
    public String send(final Object data, String messageTag) {
        return send(data, messageTag, DeliveryPolicy.AT_LEAST_ONE);
    }

    @Override
    public String send(Object data, String messageTag, DeliveryPolicy deliveryPolicy) {
        return send(data, messageTag, deliveryPolicy, null);
    }

    @Override
    public String send(Object data, String messageTag, DeliveryPolicy deliveryPolicy, String messageID) {
        final Message message = new Message(data, messageTag, deliveryPolicy, messageID);
        synchronized (box) {
            if (messageID != null)
                cancel(messageID);
            box.add(0, message);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (box) {
                    if (box.remove(message)) {
                        handleMessage(message);
                    }
                }
            }
        });
        return message.id;
    }

    @Override
    public String sendSync(Object data) {
        return sendSync(data, null);
    }

    @Override
    public String sendSync(Object data, String messageTag) {
        return sendSync(data, messageTag, DeliveryPolicy.AT_LEAST_ONE);
    }

    @Override
    public String sendSync(Object data, String messageTag, DeliveryPolicy deliveryPolicy) {
        return sendSync(data, messageTag, deliveryPolicy, null);
    }

    @Override
    public String sendSync(Object data, String messageTag, DeliveryPolicy deliveryPolicy, String messageID) {
        Message message = new Message(data, messageTag, deliveryPolicy, messageID);
        if (messageID != null)
            cancel(messageID);
        handleMessage(message);
        return message.id;
    }

    private void handleMessage(Message message) {
        deliverMessage(message);
        if (!message.isDelivered()) {
            synchronized (box) {
                box.add(0, message);
            }
        }
    }

    @Override
    public boolean cancel(String id) {
        synchronized (box) {
            for (int i = box.size() - 1; i >= 0; i--) {
                if (box.get(i).id.equals(id)) {
                    box.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean clearEventBox() {
        synchronized (box) {
            box.clear();
        }
        return false;
    }

    @Override
    public void registerReceiver(EventBusReceiver eventBusReceiver) {
        ArrayList<EventBusReceiver> eventBusReceivers = new ArrayList<>();
        eventBusReceivers.add(eventBusReceiver);
        registerReceivers(eventBusReceivers);
    }

    @Override
    public void registerReceivers(List<EventBusReceiver> eventBusReceivers) {
        this.receivers.addAll(eventBusReceivers);
        checkBox();
    }

    @Override
    public void unregisterReceiver(EventBusReceiver eventBusReceiver) {
        this.receivers.remove(eventBusReceiver);
    }

    @Override
    public void unregisterReceivers(List<EventBusReceiver> eventBusReceivers) {
        this.receivers.removeAll(eventBusReceivers);
    }


    private class Message {
        private final DeliveryPolicy deliveryPolicy;
        String messageTag;
        Object data;
        String id;
        private boolean delivered;

        public Message(Object data, String messageTag, DeliveryPolicy deliveryPolicy, String messageId) {
            this.data = data;
            this.messageTag = messageTag;
            this.deliveryPolicy = deliveryPolicy;
            if (messageId == null)
                id = UUID.randomUUID().toString();
            else
                id = messageId;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Message && ((Message) o).id.equals(id)) || id.equals(o);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (null == messageTag) {
                builder.append("receiversTag : all");
            } else {
                builder.append("receiversTag : ").append(messageTag);
            }
            if (data != null)
                builder.append("\n").append("data : ").append(data.getClass().getSimpleName()).append(" : ").append(data.toString());
            else
                builder.append("\n").append("data : ").append("null");

            return builder.toString();
        }

        public boolean isDelivered() {
            return deliveryPolicy == DeliveryPolicy.UNCHECKED || (delivered && deliveryPolicy == DeliveryPolicy.AT_LEAST_ONE);
        }

        public void setDelivered() {
            delivered = true;
        }
    }

}
