package com.leroymerlin.pandroid.demo.main.mvvm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.app.delegate.PandroidDelegate;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.models.ProductDataBinding;
import com.leroymerlin.pandroid.event.FragmentOpener;

/**
 * Created by adrien on 14/09/16.
 */
public class MvvmFragment extends PandroidFragment<FragmentOpener> implements MvvmViewModel.ExampleViewContract {

    @Override
    protected PandroidDelegate createDelegate() {
        PandroidDelegate delegate = super.createDelegate();
        delegate.addLifecycleDelegate(new MvvmViewModel());
        return delegate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mvvm, container, false);
    }


    @Override
    public void onSubmit(final ProductDataBinding productDataBinding) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        String title = productDataBinding.id.get() + " - " + productDataBinding.name.get();
        alertDialogBuilder.setTitle(title).setMessage(title).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                productDataBinding.name.set("scie");

            }
        }).show();
    }
}
