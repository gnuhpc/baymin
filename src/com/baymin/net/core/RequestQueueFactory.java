package com.baymin.net.core;

import java.io.File;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.baymin.net.ExecutorDelivery;
import com.baymin.net.RequestQueue;
import com.baymin.net.ResponseDelivery;
import com.baymin.net.Volley;
import com.baymin.net.cache.Cache;
import com.baymin.net.cache.DiskCache;
import com.baymin.net.cache.NoCache;
import com.baymin.net.stack.HttpStack;

import java.util.concurrent.Executors;

public class RequestQueueFactory {

    public static RequestQueue getQueue(Context context, String name) {
        RequestQueue result = null;

        if (RequestOptions.DEFAULT_QUEUE.equals(name)) {
            result = getDefault(context);
        }
        if (RequestOptions.BACKGROUND_QUEUE.equals(name)) {
            result = newBackgroundQueue(context);
        }

        return result;
    }

    public static RequestQueue getDefault(Context context) {
        return Volley.newRequestQueue(context.getApplicationContext());
    }

    public static RequestQueue getImageDefault(Context context) {
        return newImageQueue(context.getApplicationContext(), null, RequestOptions.IMAGE_DEFAULT_POOL_SIZE);
    }

    public static RequestQueue getFileDefault(Context context) {
        return newFileQueue(context.getApplicationContext(), null, RequestOptions.FILE_DEFAULT_POOL_SIZE);
    }
    
    public static RequestQueue newBackgroundQueue(Context context) {
        return newBackgroundQueue(context, null, RequestOptions.DEFAULT_POOL_SIZE);
    }

    public static RequestQueue newBackgroundQueue(Context context, HttpStack stack, int threadPoolSize) {
        File externalStorageDirectory = null;
        if (!isExternalStorageAvailable()) {
            externalStorageDirectory=new File(context.getCacheDir(), RequestOptions.REQUEST_CACHE_PATH);
        } else {
            externalStorageDirectory=new File(getExternalCacheDir(context), RequestOptions.REQUEST_CACHE_PATH);
            if (!externalStorageDirectory.exists()) externalStorageDirectory.mkdirs(); 
        }
        
        // pass Executor to constructor of ResponseDelivery object
        ResponseDelivery delivery = new ExecutorDelivery(Executors.newFixedThreadPool(threadPoolSize));
        return Volley.newRequestQueue(context, stack, new DiskCache(externalStorageDirectory), threadPoolSize, delivery);
    }

    public static RequestQueue newFileQueue(Context context, HttpStack stack, int threadPoolSize) {
        return Volley.newRequestQueue(context, stack, new NoCache(), threadPoolSize);
    }
    
    public static RequestQueue newImageQueue(Context context, HttpStack stack, int threadPoolSize) {
        // define cache folder
        Cache diskCache = null;
        if (!isExternalStorageAvailable()) {
            diskCache = new NoCache();
        } else {
            File externalStorageDirectory=new File(getExternalCacheDir(context), RequestOptions.IMAGE_CACHE_PATH);
            if (!externalStorageDirectory.exists()) externalStorageDirectory.mkdirs();
            diskCache = new DiskCache(externalStorageDirectory, RequestOptions.DEFAULT_DISK_USAGE_BYTES);
        }

        return Volley.newRequestQueue(context, stack, diskCache, threadPoolSize);
    }
    
    public static File getExternalCacheDir(Context context) {
        File externalStorageDirectory = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            externalStorageDirectory = context.getExternalCacheDir();
        }
        
        if (externalStorageDirectory == null) {
            // API Level <8 Equivalent of context.getExternalCacheDir()
            externalStorageDirectory = Environment.getExternalStorageDirectory();
            return new File(externalStorageDirectory, "Android/data/" + context.getPackageName()+ "/cache");
        }
        
        return externalStorageDirectory;
    }
    
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
