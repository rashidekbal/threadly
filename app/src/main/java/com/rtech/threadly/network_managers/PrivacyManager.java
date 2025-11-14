package com.rtech.threadly.network_managers;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;

public class PrivacyManager {
    public static void setPrivate(NetworkCallbackInterface callbackInterface){
        String url= ApiEndPoints.SET_PRIVATE;
        AndroidNetworking.get(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterface.onError(anError.getMessage());
                    }
                });

    }
    public static void setPublic(NetworkCallbackInterface callbackInterface){
        String Url=ApiEndPoints.SET_PUBLIC;
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization" ,"Bearer "+PreferenceUtil.getJWT())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterface.onError(anError.getMessage());

                    }
                });
    }
}
