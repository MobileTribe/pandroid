package com.leroymerlin.pandroid.demo.main.mvvm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.leroymerlin.pandroid.demo.BR;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.event.FragmentOpener;
import com.leroymerlin.pandroid.mvvm.Layout;
import com.leroymerlin.pandroid.mvvm.PandroidVMFragment;
import com.leroymerlin.pandroid.mvvm.ViewModelId;

/**
 * Created by adrien on 14/09/16.
 */
@Layout(R.layout.fragment_mvvm)
@ViewModelId(BR.productViewModel)
public class MvvmFragment extends PandroidVMFragment<FragmentOpener, ProductViewModel, ProductViewModelContract> implements ProductViewModelContract {

    @Override
    public ProductViewModelContract initViewModelContract() {
        return this;
    }

    @Override
    public ProductViewModel initViewModel(ProductViewModelContract viewModelContract) {
        return new ProductViewModel(viewModelContract);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadProduct();
    }

    public void onSubmit(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("" + viewModel.name.get()).setMessage(viewModel.name.get()).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                viewModel.name.set("wrong");

            }
        }).show();
    }
}
