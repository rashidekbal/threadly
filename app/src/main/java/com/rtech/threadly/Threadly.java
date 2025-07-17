package com.rtech.threadly;

import android.app.Application;
import android.content.Context;

import com.rtech.threadly.core.Core;

public class Threadly extends Application {
    private static Threadly instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Core.init(instance.getApplicationContext());
    }
    public static Context getGlobalContext(){
        return instance.getApplicationContext();
    }
    public static Threadly getInstance(){
        return instance;
    }
}
