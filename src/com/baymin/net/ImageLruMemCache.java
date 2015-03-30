package com.baymin.net;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.baymin.net.toolbox.ImageLoader.ImageCache;

public class ImageLruMemCache extends LruCache<String, Bitmap> implements ImageCache {

    public ImageLruMemCache(int maxSize) {
    	//TODO 是否要用maxSize需要实际测试
        super((int) (Runtime.getRuntime().maxMemory() / 1024) / 8);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }
    
    public boolean contains(String key){
    	return get(key) != null;
    }

    public Bitmap getBitmap(String url) {
        return get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

}