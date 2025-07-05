package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.androidnetworking.AndroidNetworking;

import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import com.rtech.gpgram.constants.ApiEndPoints;

import org.json.JSONObject;

import java.io.File;

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
    public void uploadImagePost(File imagefile,String caption, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.ADD_IMAGE_POST;
        AndroidNetworking.upload(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+token)
                .addMultipartFile("image",imagefile)
                .addMultipartParameter("caption",caption)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                    callback.onError(anError.toString());
                    }
                });

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
                    public void onError(com.androidnetworking.error.ANError anError) {if(BuildConfig.DEBUG){
                        Log.d("ApiError", "Error"+ anError.getMessage());

                    }
                        callbackIterface.onError(anError.getMessage());
                    }
                });
    }
    public void getUserPosts(String userId,NetworkCallbackInterfaceWithJsonObjectDelivery callbackIterface)
    {

        String url=ApiEndPoints.GET_USER_POSTS.concat(userId);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackIterface.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackIterface.onError(anError.getErrorDetail());

                    }
                });

    }
    public void getFeed(NetworkCallbackInterfaceWithJsonObjectDelivery callback){

        if(BuildConfig.DEBUG){
            Log.d("ApiData", "loading started");

        }

        String url=ApiEndPoints.GET_FEED;
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+token)
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

    public void getLoggedInUserPost(NetworkCallbackInterfaceWithJsonObjectDelivery callbackIterface)
    {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.GET_USER_POSTS.concat(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackIterface.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackIterface.onError(anError.getErrorDetail());


                    }
                });

    }
}
