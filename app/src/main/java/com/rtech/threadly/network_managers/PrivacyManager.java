package com.rtech.threadly.network_managers;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.utils.PreferenceUtil;

public class PrivacyManager {
    public static void setPrivate(NetworkCallbackInterfaceJsonObject callbackInterface){
        String url= ApiEndPoints.SET_PRIVATE;
        NetworkingProvider.get(url,PreferenceUtil.getJWT(),callbackInterface);


    }
    public static void setPublic(NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url=ApiEndPoints.SET_PUBLIC;
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callbackInterface);

    }
}
