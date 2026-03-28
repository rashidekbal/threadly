package com.rtech.threadly.utils;

import android.util.Log;

import com.rtech.threadly.constants.LogTags;

import org.json.JSONObject;


public class CentralApiErrorHandler {
    public static void handleErrorCode(int errorCode , JSONObject errorObject,String route,String timeStamp){
        String errorLog=formatErrorLog(timeStamp,route,String.valueOf(errorCode),errorObject.toString());
        LoggerUtil.writeToFile(errorLog,"ApiError");
        LoggerUtil.log("API_ERROR",errorLog);
       switch (errorCode){
           case 401:ReUsableFunctions.logoutWithoutActivity();
           break;
       }
    }


    public static String formatErrorLog(String time, String route,String errorCode,String errorBody){
        return ("response time: "+time+"\n"+"route hit: "+route+"\n"+"errorCode: "+errorCode+"\n"+"errorBody: "+errorBody+"\n"+"\n"+"\n"+"\n");

    }
}
