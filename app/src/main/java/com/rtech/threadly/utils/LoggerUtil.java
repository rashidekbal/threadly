package com.rtech.threadly.utils;

import android.util.Log;

import com.rtech.threadly.BuildConfig;

public class LoggerUtil {

    public static void LogNetworkError(String data){
        String networkTAG = "APIError";
        Log.d(networkTAG, "LogNetworkError: ");

    }
    public static void log(String TAG,String log){
        if(BuildConfig.DEBUG){
            Log.d(TAG, "log: "+log);
        }
    }
}
