package com.leroymerlin.pandroid.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.PandroidFragment;

/**
 * Created by adrien on 14/09/16.
 */
public abstract class PandroidVMFragment<T, V extends ViewModel, C extends ViewModelContract> extends PandroidFragment {

    protected ViewDataBinding viewDataBindings;
    protected V viewModel;
    protected C viewModelContract;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getClass().isAnnotationPresent(Layout.class) && getClass().isAnnotationPresent(ViewModelId.class)) {
            Layout layout = getClass().getAnnotation(Layout.class);
            ViewModelId viewModelId = getClass().getAnnotation(ViewModelId.class);

            viewModelContract = initViewModelContract();
            viewModel = initViewModel(viewModelContract);
            viewDataBindings = DataBindingUtil.inflate(inflater, layout.value(), container, false);
            View view = viewDataBindings.getRoot();
            viewDataBindings.setVariable(viewModelId.value(), viewModel);
            return view;

        } else {
            throw new RuntimeException("implements this");
        }
    }

    public abstract V initViewModel(C viewModelContract);

    public abstract C initViewModelContract();


}
