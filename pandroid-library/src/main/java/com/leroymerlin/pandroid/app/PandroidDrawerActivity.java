package com.leroymerlin.pandroid.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.annotations.EventReceiver;
import com.leroymerlin.pandroid.event.opener.ActivityOpener;


/**
 * Created by florian on 05/11/14.
 */
public abstract class PandroidDrawerActivity<T extends ActivityOpener> extends PandroidActivity<T> {

    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected abstract int getDrawerId();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawer = (DrawerLayout) findViewById(getDrawerId());
        Toolbar toolbar = (Toolbar) findViewById(getToolbarId());
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        setSupportActionBar(toolbar);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }



    public abstract int getToolbarId();


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawer.closeDrawers();
    }

    public boolean isDrawerOpen() {
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }


    @EventReceiver
    public void onDrawerEvent(DrawerEvent event) {
        if (DrawerEvent.CLOSE.equals(event)) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public enum DrawerEvent {
        OPEN,
        CLOSE
    }
}
