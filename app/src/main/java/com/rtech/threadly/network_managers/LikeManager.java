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
import com.rtech.threadly.constants.ApiEndPoints;

import org.json.JSONObject;

public class LikeManager {
    SharedPreferences loginInfo;

    private String token;
    public LikeManager(){
        loginInfo= Core.getPreference();
    }
    private String getToken(){
        return loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }

    public void likePost(int postId, NetworkCallbackInterface callbackIterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.LIKE_POST.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackIterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackIterface.onError(anError.getMessage());

                    }
                });

}

    public void UnlikePost(int postId, NetworkCallbackInterface callbackIterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.UNLIKE_POST.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackIterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackIterface.onError(anError.getMessage());

                    }
                });

    }

    public void LikeAComment(int commentId, NetworkCallbackInterface callbackIterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.LIKE_COMMENT.concat(Integer.toString(commentId));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackIterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackIterface.onError(anError.getMessage());

                    }
                });

    }
    public void UnLikeAComment(int commentId, NetworkCallbackInterface callbackIterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.UNLIKE_COMMENT.concat(Integer.toString(commentId));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackIterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackIterface.onError(anError.getMessage());

                    }
                });

    }
    public void LikeStory(int StoryId,NetworkCallbackInterface callbackInterface){
        String Url=ApiEndPoints.LIKE_STORY+Integer.toString(StoryId);
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
callbackInterface.onError(anError.toString());
                    }
                });
    }
    public void UnLikeStory(int StoryId,NetworkCallbackInterface callbackInterface){
        String Url=ApiEndPoints.UNLIKE_STORY+Integer.toString(StoryId);
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
callbackInterface.onError(anError.toString());
                    }
                });
    }




}
