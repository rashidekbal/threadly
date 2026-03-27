package com.rtech.threadly.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.Threadly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

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
    public static void writeToFile(String data){
        try {
            File dir=Threadly.getGlobalContext().getExternalFilesDir(null);
            Date date=new Date();
            File file=new File(dir,date.toString()+".txt");

            FileOutputStream fileOutputStream= new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        }catch (IOException exception){
            LoggerUtil.log("IO_Exception",exception.toString());

        }

    }
}
