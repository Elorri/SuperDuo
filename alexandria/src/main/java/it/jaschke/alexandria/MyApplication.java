package it.jaschke.alexandria;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Elorri on 07/01/2016.
 */
public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}