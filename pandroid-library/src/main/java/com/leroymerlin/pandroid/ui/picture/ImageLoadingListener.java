//tag::ImageLoadingListener[]
package com.leroymerlin.pandroid.ui.picture;

import android.view.View;
//end::ImageLoadingListener[]

/**
 * @author Adrien Le Roy
 */
//tag::ImageLoadingListener[]
public interface ImageLoadingListener {

    void onLoadingFailed(String imageUri, View view);

    void onLoadingComplete(String imageUri, View view);

}
//end::PictureManager[]
