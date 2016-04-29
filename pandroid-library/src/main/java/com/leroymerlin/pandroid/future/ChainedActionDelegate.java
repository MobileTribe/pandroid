package com.leroymerlin.pandroid.future;

import java.util.ArrayList;
import java.util.List;

public class ChainedActionDelegate<R> extends CancellableActionDelegate<R> {


    private final StartDelegate<R> startDelegate;

    List<Object> delegates = new ArrayList<>();

    private ChainedActionDelegate(StartDelegate<R> delegate) {
        super();
        startDelegate = delegate;
    }

    @Override
    protected void success(R result) {
        while (!delegates.isEmpty()) {
            Object delegate = delegates.remove(0);
            if (delegate instanceof SuccessDelegate) {
                ((SuccessDelegate) delegate).success(result, this);
                break;
            } else if (delegate instanceof ActionDelegate) {
                ((ActionDelegate) delegate).onSuccess(result);
                break;
            }
        }
    }

    @Override
    protected void error(Exception e) {
        while (!delegates.isEmpty()) {
            Object delegate = delegates.remove(0);
            if (delegate instanceof ErrorDelegate) {
                ((ErrorDelegate) delegate).error(e, this);
                break;
            } else if (delegate instanceof ActionDelegate) {
                ((ActionDelegate) delegate).onError(e);
                break;
            }
        }
    }

    public <T> ChainedActionDelegate<T> then(SuccessDelegate<R, T> successDelegate) {
        delegates.add(successDelegate);
        return (ChainedActionDelegate<T>) this;
    }

    public ActionStarter finish(ActionDelegate<R> actionDelegate) {
        delegates.add(actionDelegate);
        return new ActionStarter() {
            @Override
            public void start() {
                ChainedActionDelegate.this.start();
            }
        };
    }

    public void start() {
        startDelegate.start(ChainedActionDelegate.this);
    }

    public ChainedActionDelegate<R> error(ErrorDelegate errorDelegate) {
        delegates.add(errorDelegate);
        return (ChainedActionDelegate<R>) this;
    }

    public static <T> ChainedActionDelegate<T> create(StartDelegate<T> delegate) {
        return new ChainedActionDelegate<T>(delegate);

    }

    public interface ActionStarter {
        void start();
    }

    public interface StartDelegate<T> {
        void start(ActionDelegate<T> next);
    }


    public interface SuccessDelegate<R, T> {
        void success(R result, ActionDelegate<T> next);
    }


    public interface ErrorDelegate<T> {
        void error(Exception e, ActionDelegate<T> next);
    }

}
