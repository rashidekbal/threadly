package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;


import com.androidnetworking.AndroidNetworking;

import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import com.rtech.gpgram.constants.ApiEndPoints;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentsManager {
    // This class is responsible for managing comments on posts.
    // It will handle fetching, adding, and deleting comments.
    SharedPreferences loginInfo;
    Context context;

    private String token;

    public CommentsManager(Context c){
        this.context=c;
        AndroidNetworking.initialize(context);
        loginInfo=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.token=loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");


    }

    public void getCommentOf(int postId, NetworkCallbackInterfaceWithJsonObjectDelivery callback) {
        String url =ApiEndPoints.GET_COMMENTS.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization", "Bearer " + token)
                .build()
                .getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(com.androidnetworking.error.ANError anError) {
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
                    .addHeaders("Authorization", "Bearer " + token)
                    .addApplicationJsonBody(data)
                    .build()
                    .getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackIterface.onSuccess(response);
                        }

                        @Override
                        public void onError(com.androidnetworking.error.ANError anError) {
                            callbackIterface.onError(anError.getMessage());
                        }
                    });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
