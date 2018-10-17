package com.leroymerlin.pandroid.templates.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.annotations.BindLifeCycleDelegate;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.event.OnBackListener;

import androidx.annotation.Nullable;

public class TemplateFragment extends PandroidFragment<TemplateFragmentOpener> implements TemplateFragmentPresenter.PresenterView, OnBackListener {

    @BindLifeCycleDelegate
    TemplateFragmentPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
