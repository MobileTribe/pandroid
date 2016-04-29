//tag::PictureManager[]
package com.leroymerlin.pandroid.ui.picture;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
//end::PictureManager[]
/**
 * Created by Brahim A. on 01/12/2015.
 */
//tag::PictureManager[]
public interface PictureManager {


    void load(String href, ImageView imageView);

    void load(String href, ImageView imageView, @DrawableRes int placeholder, @DrawableRes int error, ImageLoadingListener imageLoadingListener);

    void load(Activity activity, String href, ImageView imageView);

    void load(Activity activity, String href, ImageView imageView, @DrawableRes int placeholder, @DrawableRes int error, ImageLoadingListener imageLoadingListener);

    void load(Fragment fragment, String href, ImageView imageView);

    void load(Fragment fragment, String href, ImageView imageView, @DrawableRes int placeholder, @DrawableRes int error, ImageLoadingListener imageLoadingListener);

    void remove(String href);

    void clearCache();
}
//end::PictureManager[]
