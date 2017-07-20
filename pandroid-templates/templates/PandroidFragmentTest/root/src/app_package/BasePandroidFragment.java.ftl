package ${packageName}.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.annotations.BindLifeCycleDelegate;
import com.leroymerlin.pandroid.app.PandroidFragment;

<#if applicationPackage??>
import ${applicationPackage}.R;
</#if>


public class ${fragmentClass} extends PandroidFragment<${openerClass}> implements ${presenterClass}.PresenterView {

    @BindLifeCycleDelegate
    ${presenterClass} presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

}
