package com.rtech.threadly.utils;

import com.rtech.threadly.constants.LogTags;

import org.json.JSONObject;


public class CentralApiErrorHandler {
    public static void handleErrorCode(int errorCode , JSONObject errorObject){
       switch (errorCode){
           case 401:ReUsableFunctions.logoutWithoutActivity();
           break;
           case 404:LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"route not found");
           break;
       }
    }
}
