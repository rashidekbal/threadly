package com.rtech.threadly.network_managers;


import android.content.SharedPreferences;
import android.util.Log;


import com.androidnetworking.AndroidNetworking;

import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import com.rtech.threadly.constants.ApiEndPoints;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentsManager {
    // This class is responsible for managing comments on posts.
    // It will handle fetching, adding, and deleting comments.
    SharedPreferences loginInfo;




    public CommentsManager(){

        loginInfo= Core.getPreference();

    }
    private String getToken(){
        return loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }

    public void getCommentOf(int postId, NetworkCallbackInterfaceWithJsonObjectDelivery callback) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url =ApiEndPoints.GET_COMMENTS.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization", "Bearer " + getToken())
                .build()
                .getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                       
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(com.androidnetworking.error.ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getMessage());
                    }
                });
    }
    public void addComment(int postId, String comment, NetworkCallbackInterfaceWithJsonObjectDelivery callbackIterface){
        String url = ApiEndPoints.ADD_COMMENT;
        JSONObject data=new JSONObject();
        try {
            data.put("postid", postId);
            data.put("comment", comment);

            AndroidNetworking.post(url)
                    .setPriority(com.androidnetworking.common.Priority.HIGH)
                    .addHeaders("Authorization", "Bearer " + getToken())
                    .addApplicationJsonBody(data)
                    .build()
                    .getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackIterface.onSuccess(response);
                        }

                        @Override
                        public void onError(com.androidnetworking.error.ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callbackIterface.onError(anError.getMessage());
                        }
                    });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
