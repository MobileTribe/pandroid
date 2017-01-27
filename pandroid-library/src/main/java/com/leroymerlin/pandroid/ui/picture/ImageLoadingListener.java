//tag::ImageLoadingListener[]
package com.leroymerlin.pandroid.ui.picture;

import android.widget.ImageView;
//end::ImageLoadingListener[]

/**
 * @author Adrien Le Roy
 */
//tag::ImageLoadingListener[]
public interface ImageLoadingListener<T> {

    void onLoadingFailed(T imageUri, ImageView view);

    void onLoadingComplete(T imageUri, ImageView view);

}
//end::PictureManager[]
