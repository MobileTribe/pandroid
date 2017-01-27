package com.leroymerlin.pandroid.ui.picture;

import android.widget.ImageView;

/**
 * Inspired from uil
 * @author Adrien Le Roy
 */
public class SimpleImageLoadingListener<T> implements ImageLoadingListener<T> {


    @Override
    public void onLoadingFailed(T imageUri, ImageView view) {
        // Empty implementation
    }

    @Override
    public void onLoadingComplete(T imageUri, ImageView view) {
        // Empty implementation
    }
}