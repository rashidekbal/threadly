package com.rtech.threadly.network_managers;


import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.BuildConfig;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import org.json.JSONObject;

public class ProfileManager {
    SharedPreferences loginInfo;

    public ProfileManager(){
        this.loginInfo= Core.getPreference();
    }
    private String getToken(){
       return loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }

    public final void GetProfile(String Userid, NetworkCallbackInterfaceJsonObject callback){
        String url= ApiEndPoints.GET_PROFILE.concat(Userid);
        AndroidNetworking.get(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer "+getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getErrorCode());

                    }
                });


    }
    public final void GetProfileByUuid(String uuid, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url= ApiEndPoints.GET_PROFILE_BY_UUID.concat(uuid);
        AndroidNetworking.get(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer "+getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getMessage());

                    }
                });


    }
    public final void getLoggedInUserProfile(NetworkCallbackInterfaceWithJsonObjectDelivery callback){

        String url=ApiEndPoints.GET_LOGGED_IN_USER_PROFILE;
        AndroidNetworking.get(url)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization", "Bearer "+getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener(){

                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getMessage());

                    }
                });

    }

}
