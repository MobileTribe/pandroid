package com.leroymerlin.pandroid.future;

import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by paillardf on 08/10/14.
 */
public  abstract class ConcatActionDelegate<P, W, R> implements ActionDelegate<W> {
    LogWrapper logWrapper = PandroidLogger.getInstance();

    private static final String TAG = ConcatActionDelegate.class.getSimpleName();
    protected final Stack<P> stack;
    protected List<R> result = new ArrayList<R>();
    private final ActionDelegate<List<R>> delegate;

    public ConcatActionDelegate(Stack<P> stack, ActionDelegate<List<R>> delegate) {
        this.delegate = delegate;
        this.stack = stack;
    }

    @Override
    public void onSuccess(W result) {
        if(continueStack(result))
            next();
    }

    protected abstract boolean continueStack(W result);

    @Override
    public void onError(Exception e) {
        logWrapper.e(TAG, e.getMessage(), e);
        next();
    }

    protected void next(){
        if(stack.isEmpty()){
            delegate.onSuccess(result);
        }else{
            onNext(stack.pop());
        }
    }

    public void perform(){
        next();
    }

    public abstract void onNext(P param);
}
