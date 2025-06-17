package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;


import com.androidnetworking.AndroidNetworking;

import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import com.rtech.gpgram.constants.ApiEndPoints;

public class PostsManager {
    SharedPreferences loginInfo;
    Context context;
    private String token;
    public PostsManager(Context c) {
        this.context=c;
        AndroidNetworking.initialize(context);
        loginInfo=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.token=loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }

    public void getPostWithId(int postId, NetworkCallbackInterfaceWithJsonObjectDelivery callbackIterface){

        String url= ApiEndPoints.GET_POST_BY_ID.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization","Bearer "+token)
                .build().getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {

                        callbackIterface.onSuccess(response);
                    }

                    @Override
                    public void onError(com.androidnetworking.error.ANError anError) {
                        callbackIterface.onError(anError.getMessage());
                    }
                });
    }
}
