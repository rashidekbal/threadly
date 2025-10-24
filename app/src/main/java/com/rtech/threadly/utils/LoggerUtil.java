package com.rtech.threadly.utils;

import android.util.Log;

public class LoggerUtil {

    public static void LogNetworkError(String data){
        String networkTAG = "APIError";
        Log.d(networkTAG, "LogNetworkError: ");

    }
    public static void log(String Tag,String log){
        if(Tag==null){
            Tag="defautlTag";
        }
        Log.d(Tag,log);

    }
}
