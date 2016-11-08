package com.leroymerlin.pandroid.demo.main.mvp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.globals.review.ReviewManager;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.pandroid.annotations.BindLifeCycleDelegate;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Mehdi on 08/11/2016.
 */
//tag::PandroidBindLifeCycleDelegateAnnotationUseCase[]
public class PresenterFragment extends PandroidFragment<FragmentOpener> implements TestPresenter.TestPresenterView{


    @BindLifeCycleDelegate
    LoggerLifeCycleDelegate mLoggerLifeCycleDelegate = new LoggerLifeCycleDelegate();

    @Inject
    @BindLifeCycleDelegate
    TestPresenter mTestPresenter;

    //end::PandroidBindLifeCycleDelegateAnnotationUseCase[]

    @BindView(R.id.text1)
    TextView mTextView;

    @Inject
    ReviewManager mReviewManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected PandroidDelegate createDelegate() {
        return super.createDelegate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_presenter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTestPresenter.load();
    }

    @Override
    public void onDataLoaded(String value) {
        mTextView.setText(value);
        mTextView.setTextColor(Color.BLUE);
    }

    @Override
    public void onError(String error) {
        mTextView.setText(error);
        mTextView.setTextColor(Color.RED);
    }

}
