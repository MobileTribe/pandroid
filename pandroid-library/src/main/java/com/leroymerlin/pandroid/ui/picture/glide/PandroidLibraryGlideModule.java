package com.leroymerlin.pandroid.ui.picture.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.LibraryGlideModule;
import com.leroymerlin.pandroid.PandroidApplication;
import com.leroymerlin.pandroid.ui.picture.glide.okhttp.OkHttpUrlLoader;

import java.io.InputStream;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;

/**
 * Created by florian on 08/03/2018.
 */
@GlideModule
public class PandroidLibraryGlideModule extends LibraryGlideModule {


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        setupOkHttpClient(context, registry);
    }

    static boolean setupOkHttpClient = false;

    protected static void setupOkHttpClient(@NonNull Context context, @NonNull Registry registry) {
        if (!setupOkHttpClient) {
            setupOkHttpClient = true;
            OkHttpClient.Builder builder = PandroidApplication.getInjector(context).getBaseComponent().okHttpClientBuilder();
            registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(builder.build()));
        }
    }
}
