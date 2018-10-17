package com.leroymerlin.pandroid.templates.feature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leroymerlin.pandroid.annotations.BindLifeCycleDelegate;
import com.leroymerlin.pandroid.app.RxPandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.ui.list.recyclerview.HolderFactory;
import com.leroymerlin.pandroid.ui.list.recyclerview.PandroidAdapter;
import com.leroymerlin.pandroid.ui.list.recyclerview.RecyclerHolder;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class FeatureFragment extends RxPandroidFragment<FeatureFragmentOpener> implements FeatureFragmentPresenter.PresenterView {

    @Inject
    ToastManager toastManager;

    @BindLifeCycleDelegate
    FeatureFragmentPresenter presenter;

    @BindView(R.id.feature_loader)
    View loader;

    @BindView(R.id.feature_retry)
    View retry;

    @BindView(R.id.feature_rv)
    RecyclerView recyclerView;

    private PandroidAdapter<FeatureModel> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feature, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.featureNameTitle);

        adapter = new PandroidAdapter<>();
        adapter.registerFactory(FeatureModel.class, HolderFactory.create(FeatureModelHolder.class));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoader() {
        retry.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        loader.setVisibility(View.GONE);
    }


    @Override
    public void onDataLoaded(List<FeatureModel> featureModel) {
        adapter.clear();
        adapter.addAll(featureModel);
    }

    @OnClick(R.id.feature_retry)
    public void reload() {
        presenter.reload();
    }

    @Override
    public void onError(String message) {
        retry.setVisibility(View.VISIBLE);
        toastManager.makeToast(getActivity(), message, null, R.style.Toast_Error);
    }


    private static class FeatureModelHolder extends RecyclerHolder<FeatureModel>{

        private TextView cellTv;

        public FeatureModelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.cell_feature, parent, false));
        }

        @Override
        protected void bindView(View view) {
            super.bindView(view);
            cellTv = view.findViewById(R.id.feature_cell_tv);

        }

        @Override
        public void setContent(FeatureModel content, int index) {
            cellTv.setText(content.getTitle());
        }
    }
}
