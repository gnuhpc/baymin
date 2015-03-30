package com.baymin.net;

import android.content.Context;

import com.baymin.log.AppLogger;
import com.baymin.net.toolbox.ImageLoader;

public class VolleyHelper {
	private static final int MAX_IMAGE_CACHE_ENTIRES  = 100;
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    
    public static void initVolley(Context ctx) {
    	mRequestQueue = Volley.newRequestQueue(ctx);
    	mImageLoader = new ImageLoader(mRequestQueue, new ImageLruMemCache(MAX_IMAGE_CACHE_ENTIRES));
	}
    
    public static RequestQueue getRequestQueue() {
		if (mRequestQueue!=null) {
			return mRequestQueue;
		}
		else{
			AppLogger.e("Request Queue has not been initiated!");
			throw new IllegalAccessError("Request Queue has not been initiated!");
		}
	}
    
    public static ImageLoader getImageLoader() {
		if (mImageLoader!=null) {
			return mImageLoader;
		}
		else{
			AppLogger.e("Image Loader has not been initiated!");
			throw new IllegalAccessError("Image Loader has not been initiated!");
		}
	}
    
    public static void addRequest(Request<?> request) {
    	getRequestQueue().add(request);
	}
    
}
