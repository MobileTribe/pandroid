package com.leroymerlin.pandroid.ui.picture.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.GlideModule;

import androidx.annotation.NonNull;

/**
 * Support to autoconfigure glide OkHttpClient if no glide module is declared in client app
 */
public class PandroidGlideModule implements GlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        PandroidLibraryGlideModule.setupOkHttpClient(context, registry);
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }
}
