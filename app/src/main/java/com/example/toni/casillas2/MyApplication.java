package com.example.toni.casillas2;

/**
 * Created by toni on 01/01/2018.
 */
import android.app.Application;
import android.content.Context;
/** Añadir al manifest
 * android:name="com.example.toni.volley.MyApplication"
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
    }

    public static MyApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}