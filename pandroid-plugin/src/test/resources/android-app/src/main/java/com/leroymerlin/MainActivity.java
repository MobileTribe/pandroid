package com.leroymerlin;

import android.os.Bundle;

import com.leroymerlin.pandroid.app.PandroidActivity;
import com.leroymerlin.pandroid.event.opener.ActivityOpener;
import com.leroymerlin.pandroid.sample.R;


public class MainActivity extends PandroidActivity<ActivityOpener> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
