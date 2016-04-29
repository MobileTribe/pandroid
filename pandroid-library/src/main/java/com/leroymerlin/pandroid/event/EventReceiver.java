package com.leroymerlin.pandroid.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This receiver handler Event. It is responsible of packaging/unpackaging event into Intent.
 * EventReceiver is able to make the best match of method 'onReceive' based on the type of event
 *
 * Created by florian on 22/01/15.
 */
public class EventReceiver<T extends ReceiversProvider> extends AbstractReceiver<T> {

    public static final String TAG = "EventReceiver";
    public static final String ARG_EVENT = TAG + ".ARG_EVENT";

    List<String> filter = new ArrayList<String>();


    public void addEvent(Event event) {
        addActionTag(event.getFilterTag());
    }

    public void addActionTag(String tag) {
        filter.add(tag);
    }

    private void invokeReceive(Event event, Class<? extends Event> eventClass) {
        try {
            Method onReceive = this.getClass().getMethod("onReceive", eventClass);
            onReceive.invoke(this, event);
        } catch (NoSuchMethodException e) {
            if (eventClass.equals(Event.class))
                onReceive(event);
            else
                invokeReceive(event, (Class<? extends Event>) eventClass.getSuperclass());
        } catch (InvocationTargetException e) {
            logWrapper.e(TAG, e);
        } catch (IllegalAccessException e) {
            logWrapper.e(TAG, e);
        }

    }

    /**
     * default onReceive methode
     * You can add onReceive in your custom Receiver to catch specifique event.
     * @param event
     */
    public void onReceive(Event event) {
        logWrapper.v(TAG, "Basic event received");
    }


    @Override
    public boolean handle(Object data) {
        if(data instanceof Event && filter.contains(((Event) data).getFilterTag())){
            invokeReceive(((Event) data), ((Event) data).getClass());
            return true;
        }
        return false;
    }
}
