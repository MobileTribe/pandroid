package com.leroymerlin.pandroid.event;

/**
 * Created by paillardf on 17/06/2014.
 */
public interface OnBackListener {
    /**
     * Call when activity handle on back pressed
     * @return true if the fragment handle the event, false otherwise
     */
    public boolean onBackPressed();
}
