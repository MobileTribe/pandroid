package com.leroymerlin.pandroid.ui.picture.impl;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.leroymerlin.pandroid.ui.picture.ImageLoadingListener;
import com.leroymerlin.pandroid.ui.picture.PictureManager;

import javax.inject.Inject;


/**
 * <p>This implementation of {@link PictureManager} is backed by the Glide
 * library {@link https://github.com/bumptech/glide}.</p>
 * <p/>
 * <p>For performance reasons, consider using one of the <code>load</code> method that takes a
 * specific context like <code>Activity</code>, <code>Fragment</code> ... so that the load can be
 * tied to the context's lifecyle.</p>
 * <p/>
 * Created by brahim on 01/12/2015.
 *
 * @author brahim.aitelhaj@ext.leroymerlin.fr
 */
public class GlidePictureManagerImpl implements PictureManager, GlideModule {

    private int placeHolder;
    private int errorImage;
    private final Context context;

    @Inject
    public GlidePictureManagerImpl(Context context) {
        this.context = context;
    }

    public static final String TAG = GlidePictureManagerImpl.class.getSimpleName();

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
    }

    @Override
    public void load(String href, ImageView imageView) {
        load(href, imageView, placeHolder, errorImage, null);
    }

    @Override
    public void load(String href, ImageView imageView, @DrawableRes int placeholder, @DrawableRes int errorDrawable, ImageLoadingListener imageLoadingListener) {
        load(Glide.with(context), href, imageView, placeholder, errorDrawable, imageLoadingListener);
    }

    @Override
    public void load(Activity activity, String href, ImageView imageView) {
        load(Glide.with(activity), href, imageView, placeHolder, errorImage, null);
    }

    @Override
    public void load(Activity activity, String href, ImageView imageView, @DrawableRes int placeholder, @DrawableRes int error, ImageLoadingListener imageLoadingListener) {
        load(Glide.with(activity), href, imageView, placeholder, error, imageLoadingListener);
    }

    @Override
    public void load(Fragment fragment, String href, ImageView imageView) {
        load(Glide.with(fragment), href, imageView, placeHolder, errorImage, null);
    }

    @Override
    public void load(Fragment fragment, String href, ImageView imageView, @DrawableRes int placeholder, @DrawableRes int error, ImageLoadingListener imageLoadingListener) {
        load(Glide.with(fragment), href, imageView, placeHolder, errorImage, imageLoadingListener);
    }

    private void load(RequestManager requestManager, final String href, final ImageView imageView, @DrawableRes int placeholder, @DrawableRes int errorDrawable, final ImageLoadingListener imageLoadingListener) {
        DrawableRequestBuilder<String> builder = requestManager.load(href).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false);
        if (placeholder == 0) {
            placeholder = this.placeHolder;
        }
        if (errorDrawable == 0) {
            errorDrawable = this.errorImage;
        }

        if (placeholder > 0)
            builder.placeholder(placeholder);
        if (errorDrawable > 0)
            builder.error(errorDrawable);
        builder.crossFade();


        if (imageLoadingListener != null) {
            builder.listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    imageLoadingListener.onLoadingFailed(href, imageView);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    imageLoadingListener.onLoadingComplete(href, imageView);
                    return false;
                }
            });
        }

        if (imageView == null) {
            builder.into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                }
            });
        } else {
            builder.into(imageView);
        }
    }


    @Override
    public void remove(String href) {
        throw new IllegalStateException("Not implemented with glide see : https://github.com/bumptech/glide/wiki/Caching-and-Cache-Invalidation");
    }

    @Override
    public void clearCache() {
        Glide.get(context).clearDiskCache();
        Glide.get(context).clearMemory();
    }

    public void setPlaceHolder(@DrawableRes int placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void setErrorImage(@DrawableRes int errorImage) {
        this.errorImage = errorImage;
    }
}
