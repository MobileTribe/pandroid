package com.leroymerlin.pandroid.ui.picture.impl;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.leroymerlin.pandroid.ui.picture.ImageLoadingListener;
import com.leroymerlin.pandroid.ui.picture.PictureManager;

import java.net.URL;
import java.util.Map;

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

    public static final String TAG = "GlidePictureManagerImpl";

    private final Context context;

    private Loader defaultLoader;

    @Inject
    public GlidePictureManagerImpl(Context context) {
        this.context = context;
    }

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
    public void configure(Loader defaultLoader) {
        this.defaultLoader = defaultLoader;
    }

    @Override
    public void clearCache() {
        Glide.get(context).clearDiskCache();
        Glide.get(context).clearMemory();
    }

    @Override
    public Loader loader() {
        return new Loader(defaultLoader) {
            @Override
            public void loadInternal() {
                RequestManager requestManager = null;
                Object loadContext = null;
                if (this.loadContext != null) {
                    loadContext = this.loadContext.get();
                }
                if (loadContext instanceof Fragment) {
                    requestManager = Glide.with((Fragment) loadContext);
                } else if (loadContext instanceof Activity) {
                    requestManager = Glide.with((Activity) loadContext);
                } else {
                    requestManager = Glide.with(context);
                }


                LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder();
                for (Map.Entry<String, String> value : this.headers.entrySet()) {
                    headerBuilder.addHeader(value.getKey(), value.getValue());
                }

                Object glideUrl = null;
                Object sourceModel = this.sourceModel;
                if (sourceModel instanceof String) {
                    glideUrl = new GlideUrl((String) sourceModel, headerBuilder.build());
                } else if (sourceModel instanceof URL) {
                    glideUrl = new GlideUrl((URL) sourceModel, headerBuilder.build());
                } else {
                    glideUrl = sourceModel;
                }


                DrawableRequestBuilder builder = requestManager.load(glideUrl);

                builder.diskCacheStrategy(this.diskCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE);
                builder.skipMemoryCache(!this.memoryCache);

                if (this.placeHolder > 0) {
                    builder.placeholder(this.placeHolder);
                }
                if (this.errorImage > 0) {
                    builder.error(this.errorImage);
                }
                if (this.animated) {
                    if (this.animator > 0) {
                        builder.animate(this.animator);
                    } else {
                        builder.crossFade();
                    }
                } else {
                    builder.dontAnimate();
                }

                if (this.scaleType == ImageView.ScaleType.FIT_CENTER) {
                    builder.fitCenter();
                } else if (this.scaleType == ImageView.ScaleType.CENTER_CROP) {
                    builder.centerCrop();
                }
                if (this.target != null) {
                    if (this.scaleType == null) {
                        builder.dontTransform();
                    } else {
                        this.target.setScaleType(this.scaleType);
                    }
                }

                final ImageLoadingListener loaderListener = this.listener;
                final ImageView loaderTarget = this.target;
                if (loaderListener != null) {
                    builder.listener(new RequestListener<Object, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                            loaderListener.onLoadingFailed(model, loaderTarget);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            loaderListener.onLoadingComplete(model, loaderTarget);
                            return false;
                        }
                    });
                }

                if (loaderTarget == null) {
                    builder.into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                        }
                    });
                } else {
                    builder.into(loaderTarget);
                }
            }
        };
    }


}
