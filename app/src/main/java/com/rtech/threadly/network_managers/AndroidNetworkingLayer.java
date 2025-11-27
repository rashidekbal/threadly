package com.rtech.threadly.network_managers;

import androidx.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class AndroidNetworkingLayer {
    public static void get(String Url,  NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

            AndroidNetworking.get(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            callbackInterfaceJsonObject.onSuccess(jsonObject);

                        }

                        @Override
                        public void onError(ANError anError) {
                            callbackInterfaceJsonObject.onError(anError.getErrorCode());

                        }
                    });
        }
    public static void post(String Url,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }
    public static void post(String Url,  JSONObject object,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                .addJSONObjectBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }
    public static void post(String Url, JSONArray array, NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                .addJSONArrayBody(array)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }

}
