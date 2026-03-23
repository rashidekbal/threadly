package com.rtech.threadly.utils;

import com.rtech.threadly.constants.LogTags;

public class CentralApiErrorHandler {
    public static void handleErrorCode(int errorCode){
        LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"error code : "+errorCode);
    }
}
