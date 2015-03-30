package com.baymin.net.core;

import android.content.Context;

import com.baymin.net.RequestQueue;
import com.baymin.net.Volley;
import com.baymin.net.cache.BitmapLruCache;
import com.baymin.net.toolbox.ImageLoader;

class ImageLoaderFactory {

    public static ImageLoader getDefault(Context context) {
        return newLoader(RequestQueueFactory.getImageDefault(context), new BitmapLruCache());
    }

    public static ImageLoader newLoader(RequestQueue queue, BitmapLruCache cache) {
        return Volley.newImageLoader(queue, cache);
    }

    public static ImageLoader newLoader(Context context, int cacheSize) {
        return newLoader(RequestQueueFactory.getImageDefault(context),
                new BitmapLruCache(cacheSize));
    }

    public static ImageLoader newLoader(Context context, BitmapLruCache cache) {
        return newLoader(RequestQueueFactory.getImageDefault(context), cache);
    }
}
