package com.leroymerlin.pandroid.demo.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.main.anim.AnimationFragment;
import com.leroymerlin.pandroid.demo.main.event.EventFragment;
import com.leroymerlin.pandroid.demo.main.list.ListOpener;
import com.leroymerlin.pandroid.demo.main.list.ListViewFragment;
import com.leroymerlin.pandroid.demo.main.list.RecyclerViewFragment;
import com.leroymerlin.pandroid.demo.main.rest.RestFragment;
import com.leroymerlin.pandroid.demo.main.scanner.ScannerFragment;

import butterknife.BindView;

/**
 * Created by florian on 08/12/15.
 */
public class NavigationLeftFragment extends PandroidFragment {

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_left, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                boolean handle = false;
                switch (item.getItemId()) {
                    case R.id.navigation_scanner:
                        startFragment(ScannerFragment.class);
                        handle = true;
                        break;
                    case R.id.navigation_event_bus:
                        startFragment(EventFragment.class);
                        handle = true;
                        break;
                    case R.id.navigation_rest:
                        startFragment(RestFragment.class);
                        handle = true;
                        break;
                    case R.id.navigation_animation:
                        startFragment(AnimationFragment.class);
                        handle = true;
                        break;
                    case R.id.navigation_list:
                        sendEvent(new ListOpener(ListViewFragment.class, 40));
                        handle = true;
                        break;
                    case R.id.navigation_recycler:
                        sendEvent(new ListOpener(RecyclerViewFragment.class, 50));
                        handle = true;
                        break;
                }
                if(handle) {
                    eventBusManager.send(handle, MainActivity.DRAWER_EVENT);
                }
                return handle;
            }
        });
    }
}
