package com.leroymerlin.pandroid.ui.picture.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.leroymerlin.pandroid.log.LogcatLogger;
import com.leroymerlin.pandroid.ui.picture.ImageLoadingListener;
import com.leroymerlin.pandroid.ui.picture.PictureManager;

import java.net.URL;
import java.util.Map;

import javax.inject.Inject;


/**
 * <p>This implementation of {@link PictureManager} is backed by the Glide
 * <p/>
 * <p>For performance reasons, consider using one of the <code>load</code> method that takes a
 * specific context like <code>Activity</code>, <code>Fragment</code> ... so that the load can be
 * tied to the context's lifecyle.</p>
 * <p/>
 * Created by brahim on 01/12/2015.
 *
 * @author brahim.aitelhaj@ext.leroymerlin.fr
 */
public class GlidePictureManagerImpl implements PictureManager {

    public static final String TAG = "GlidePictureManagerImpl";

    private final Context context;

    private Loader defaultLoader;

    @Inject
    public GlidePictureManagerImpl(Context context) {
        this.context = context;
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
                    glideUrl = new GlideUrl((String) sourceModel, headerBuilder.build()) {
                        @Override
                        public String getCacheKey() {
                            if (customCacheKey != null) {
                                return customCacheKey;
                            }
                            return super.getCacheKey();
                        }
                    };
                } else if (sourceModel instanceof URL) {
                    glideUrl = new GlideUrl((URL) sourceModel, headerBuilder.build()) {
                        @Override
                        public String getCacheKey() {
                            if (customCacheKey != null) {
                                return customCacheKey;
                            }
                            return super.getCacheKey();
                        }
                    };
                } else {
                    glideUrl = sourceModel;
                    if (sourceModel != null && customCacheKey != null) {
                        LogcatLogger.getInstance().w(TAG, "Can't set cacheKey with a custom source model : " + sourceModel.toString());
                    }
                }

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(this.diskCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                        .skipMemoryCache(!this.memoryCache);

                RequestBuilder<Drawable> builder = requestManager.load(glideUrl);

                if (this.placeHolder > 0) {
                    options = options.placeholder(this.placeHolder);
                }
                if (this.errorImage > 0) {
                    options = options.error(this.errorImage);
                }
                GenericTransitionOptions genericTransitionOptions = new GenericTransitionOptions();

                if (this.animated) {
                    if (this.animation > 0) {
                        builder = builder.transition(GenericTransitionOptions.with(animation));
                    } else {
                        builder = builder.transition(DrawableTransitionOptions.withCrossFade());
                    }
                } else {
                    builder = builder.transition(GenericTransitionOptions.<Drawable>withNoTransition());
                }


                if (this.scaleType == ImageView.ScaleType.FIT_CENTER) {
                    options = options.fitCenter();
                } else if (this.scaleType == ImageView.ScaleType.CENTER_CROP) {
                    options = options.centerCrop();
                } else if (this.scaleType == ImageView.ScaleType.CENTER_INSIDE) {
                    options = options.centerInside();
                }
                if (this.target != null && scaleType != null) {
                    this.target.setScaleType(this.scaleType);
                }
                if (this.circleCrop) {
                    options = options.circleCrop();
                }

                builder = builder.apply(options);


                final ImageLoadingListener loaderListener = this.listener;
                final ImageView loaderTarget = this.target;
                if (loaderListener != null) {
                    builder = builder.listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            loaderListener.onLoadingFailed(model, loaderTarget);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loaderListener.onLoadingComplete(model, loaderTarget);
                            return false;
                        }
                    });
                }

                if (loaderTarget == null) {
                    builder.into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        }
                    });
                } else {
                    builder.into(loaderTarget);
                }
            }
        };
    }


}
