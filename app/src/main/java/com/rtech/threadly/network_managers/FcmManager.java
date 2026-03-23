package com.rtech.threadly.network_managers;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class FcmManager {

    public static void UpdateFcmToken(String fcmToken, NetworkCallbackInterfaceJsonObject callbackInterface){
        String url= ApiEndPoints.FCM_TOKEN_UPDATE;
        JSONObject object=new JSONObject();
        try {
            object.put("token",fcmToken);
            NetworkingProvider.patch(url, PreferenceUtil.getJWT(),object,callbackInterface);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



    }

}
