package com.example.toni.casillas2;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by toni on 01/01/2018.
 */

public class VolleySingleton {
    private static VolleySingleton instance = null;
    private RequestQueue mRequestQueue;
    //para subir imagenes
    private ImageLoader imageLoader;
    private VolleySingleton()
    {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        imageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    //constructor privado
    public static  VolleySingleton getInstance()
    {
        if(instance==null)
        {
            instance=new VolleySingleton();
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }



}
