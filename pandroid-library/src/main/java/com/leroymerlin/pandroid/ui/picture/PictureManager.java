//tag::PictureManager[]
package com.leroymerlin.pandroid.ui.picture;

//end::PictureManager[]

import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brahim A. on 01/12/2015.
 */
//tag::PictureManager[]
public interface PictureManager {


    /**
     * set default values applied on new loader
     *
     * @param defaultLoader loader to use as default
     */
    void configure(Loader defaultLoader);

    /**
     * Clear disk and memory cache of image
     */
    void clearCache();

    /**
     * create a new loader with default values
     *
     * @return new loader model
     */
    Loader loader();


    abstract class Loader {


        protected Object sourceModel;
        protected WeakReference<Object> loadContext;
        protected ImageLoadingListener listener;
        protected ImageView target;

        protected Map<String, String> headers = new HashMap<>();
        protected boolean memoryCache = true;
        protected boolean diskCache = true;
        protected boolean animated = true;
        protected int placeHolder;
        protected int errorImage;
        protected int animator;
        protected ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;

        public Loader(Loader baseLoader) {
            if (baseLoader != null) {
                apply(baseLoader);
            }
        }

        /**
         *
         * @param loadContext
         * @return
         */
        public Loader context(@NotNull Object loadContext) {
            this.loadContext = new WeakReference<>(loadContext);
            return this;
        }

        /**
         *
         * @param sourceModel
         * @return
         */
        public Loader source(Object sourceModel) {
            this.sourceModel = sourceModel;
            return this;
        }

        public Loader target(ImageView target) {
            this.target = target;
            return this;
        }

        public Loader header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Loader listener(ImageLoadingListener imageLoadingListener) {
            this.listener = imageLoadingListener;
            return this;
        }

        public Loader scaleType(ImageView.ScaleType scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public Loader placeHolder(@DrawableRes int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        public Loader errorImage(@DrawableRes int errorImage) {
            this.errorImage = errorImage;
            return this;
        }

        public Loader enableCache(boolean diskCache, boolean memoryCache) {
            this.diskCache = diskCache;
            this.memoryCache = memoryCache;
            return this;
        }

        public Loader animator(@AnimatorRes int animator) {
            this.animator = animator;
            animated(animator > 0);
            return this;
        }

        public Loader animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        public Loader apply(Loader defaultLoader) {
            this.placeHolder = defaultLoader.placeHolder;
            this.errorImage = defaultLoader.errorImage;
            this.diskCache = defaultLoader.diskCache;
            this.memoryCache = defaultLoader.memoryCache;
            this.animated = defaultLoader.animated;
            this.animator = defaultLoader.animator;
            this.headers.putAll(defaultLoader.headers);
            return this;
        }

        public final void load() {
            loadInternal();
        }

        protected abstract void loadInternal();


    }


}
//end::PictureManager[]
