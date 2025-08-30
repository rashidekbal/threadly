package com.rtech.threadly.network_managers;


import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.constants.ApiEndPoints;
import org.json.JSONException;
import org.json.JSONObject;
public class FollowManager {
    SharedPreferences loginInfo;

    public FollowManager(){
        this.loginInfo= Core.getPreference();
    }
    private String getToken(){
        return loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }
    public void  follow(String userid, NetworkCallbackInterface callbackIterface) {
        String url=ApiEndPoints.FOLLOW;

        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
                    .addHeaders("Authorization", "Bearer "+getToken()).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    callbackIterface.onSuccess();

                }


                @Override
                public void onError(ANError anError) {
                    if(BuildConfig.DEBUG){
                        Log.d("ApiError", "Error"+ anError.getMessage());

                    }
                    callbackIterface.onError(anError.toString());

                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void unfollow(String userid, NetworkCallbackInterface callbackIterface) {
        String url=ApiEndPoints.UNFOLLOW;
        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);

            AndroidNetworking.post(url).setPriority(Priority.HIGH).addHeaders("Authorization", "Bearer "+getToken()).addApplicationJsonBody(packet).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("unfollowerror", response.toString());
                    callbackIterface.onSuccess();

                }


                @Override
                public void onError(ANError anError) {

                    int errorCode=anError.getErrorCode();
                    if(BuildConfig.DEBUG){
                        Log.d("ApiError", "Error"+ anError.getMessage());

                    }
                    callbackIterface.onError(anError.toString());

                }
            });



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void getFollowers(String userid, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.GET_FOLLOWERS.concat(userid);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
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
                        callback.onError(anError.getErrorDetail());

                    }
                });
    }
    public  void getFollowings(String userid, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.GET_FOLLOWINGS.concat(userid);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
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
                        callback.onError(anError.getErrorDetail());

                    }
                });
    }
}
