package com.leroymerlin.pandroid.ui.picture.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.leroymerlin.pandroid.PandroidApplication;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by florian on 01/02/2017.
 */

public class PandroidGlideModule implements GlideModule {
    private OkHttpClient client;


    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context));
        LruResourceCache memoryCache = new LruResourceCache(defaultMemoryCacheSize);
        builder.setMemoryCache(memoryCache);
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        if (client == null) {
            client = PandroidApplication.get(context).getBaseComponent().okHttpClientBuilder().build();
        }
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}
