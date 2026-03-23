package com.rtech.threadly.utils;

import com.rtech.threadly.constants.LogTags;

public class CentralApiErrorHandler {
    public static void handleErrorCode(int errorCode){
       switch (errorCode){
           case 401:ReUsableFunctions.logoutWithoutActivity();
           case 404:LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"route not found");
       }
    }
}
