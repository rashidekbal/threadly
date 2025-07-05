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
import com.rtech.gpgram.constants.ApiEndPoints;

import org.json.JSONObject;

public class LikeManager {
    SharedPreferences loginInfo;
    Context context;
    private String token;
    public LikeManager(Context c){
        this.context=c;
        AndroidNetworking.initialize(c);
        loginInfo=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.token=loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");


    }

    public void likePost(int postId, NetworkCallbackInterface callbackIterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.LIKE_POST.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+token)
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
                .addHeaders("Authorization","Bearer "+token)
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
                .addHeaders("Authorization","Bearer "+token)
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
                .addHeaders("Authorization","Bearer "+token)
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


}
