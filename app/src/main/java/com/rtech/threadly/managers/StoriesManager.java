package com.rtech.threadly.managers;

import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import org.json.JSONObject;

import java.io.File;

public class StoriesManager {
    SharedPreferences loginInfo;
    public StoriesManager(){
        this.loginInfo= Core.getPreference();
    }

    public void AddStory(File media,String Type, NetworkCallbackInterface callbackInterface){
        String Url= ApiEndPoints.ADD_STORY;
        AndroidNetworking.upload(Url)
                .addHeaders("Authorization","Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .addMultipartFile("media",media)
                .addMultipartParameter("type",Type)
                .setPriority(Priority.HIGH).build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {

                    }
                }).getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                    callbackInterface.onError(anError.getMessage().toString());
                    }
                });
    }
    public void getStories(NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterfaceWithJsonObjectDelivery){
        String Url=ApiEndPoints.GET_STORIES;
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization" , "Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callbackInterfaceWithJsonObjectDelivery.onSuccess(response);


                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceWithJsonObjectDelivery.onError(anError.getMessage());

                    }
                });
    }
    public void getStoriesOf(String Userid,NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterfaceWithJsonObjectDelivery){
        String Url=ApiEndPoints.GET_STORIES+Userid;
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization" , "Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callbackInterfaceWithJsonObjectDelivery.onSuccess(response);


                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceWithJsonObjectDelivery.onError(anError.getMessage());

                    }
                });

    }
    public void getMyStories(NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterfaceWithJsonObjectDelivery){
        String Url=ApiEndPoints.GET_My_STORIES;
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization" , "Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callbackInterfaceWithJsonObjectDelivery.onSuccess(response);


                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceWithJsonObjectDelivery.onError(anError.getMessage());

                    }
                });

    }
}
