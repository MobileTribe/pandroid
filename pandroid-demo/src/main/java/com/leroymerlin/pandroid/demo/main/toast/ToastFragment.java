package com.leroymerlin.pandroid.demo.main.toast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * Created by florian on 27/09/2016.
 */

public class ToastFragment extends PandroidFragment {


    @Inject
    ToastManager toastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toast, container, false);
    }


    @OnClick({R.id.toast_normal, R.id.toast_action, R.id.toast_loader, R.id.toast_icon})
    public void onToastClicked(final Button v) {
        switch (v.getId()) {
            case R.id.toast_normal:
                toastManager.makeToast(getActivity(), (String) v.getText(), new ToastManager.ToastListener() {
                    @Override
                    public void onDismiss() {
                        Toast.makeText(getActivity(), "onDismiss", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onActionClicked() {

                    }
                });
                break;
            case R.id.toast_action:
                toastManager.makeActionToast(getActivity(), (String) v.getText(), "action", 0, new ToastManager.ToastListener() {

                    @Override
                    public void onDismiss() {
                    }

                    @Override
                    public void onActionClicked() {
                        Toast.makeText(getActivity(), "onActionClicked", Toast.LENGTH_SHORT).show();
                    }
                }, R.style.Toast_Warm);
                break;
            case R.id.toast_loader:

                final ToastManager.ToastNotifier notifier = toastManager.makeLoaderToast(getActivity(), v.getText().toString(), false, null);
                Runnable action = new Runnable() {
                    int progress = 0;

                    @Override
                    public void run() {
                        if (progress >= 100) {
                            notifier.dismiss();
                        } else {
                            progress++;
                            notifier.setProgress(progress);
                            v.postDelayed(this, 50);
                        }
                    }
                };
                action.run();
                break;
            case R.id.toast_icon:
                toastManager.makeImageToast(getActivity(), v.getText().toString(), R.drawable.icon_light_exit, null, R.style.Toast_Error);
                break;

        }
    }
}
