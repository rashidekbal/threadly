package com.rtech.threadly.managers;


import android.content.SharedPreferences;
import android.util.Log;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.constants.ApiEndPoints;
import org.json.JSONObject;
import java.io.File;

public class PostsManager {
    SharedPreferences loginInfo;
    public PostsManager() {
        loginInfo= Core.getPreference();
    }
    private String getToken(){
        return loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }
    public void uploadImagePost(File imagefile,String caption, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.ADD_IMAGE_POST;
        AndroidNetworking.upload(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .addMultipartFile("image",imagefile)
                .addMultipartParameter("caption",caption)
                .build().setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {

                    }
                }).getAsJSONObject(new JSONObjectRequestListener() {
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

    public void getPostWithId(int postId, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface){


        String url= ApiEndPoints.GET_POST_BY_ID.concat(Integer.toString(postId));
        AndroidNetworking.get(url)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build().getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {


                        callbackInterface.onSuccess(response);
                    }

                    @Override
                    public void onError(com.androidnetworking.error.ANError anError) {if(BuildConfig.DEBUG){
                        Log.d("ApiError", "Error"+ anError.getMessage());

                    }
                        callbackInterface.onError(anError.getMessage());
                    }
                });
    }
    public void getUserPosts(String userId,NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface)
    {

        String url=ApiEndPoints.GET_USER_POSTS.concat(userId);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackInterface.onError(anError.getErrorDetail());

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
                .addHeaders("Authorization","Bearer "+getToken())
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

    public void getLoggedInUserPost(NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface)
    {
        String url=ApiEndPoints.GET_USER_POSTS.concat(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"));
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callbackInterface.onError(anError.getErrorDetail());


                    }
                });

    }
}
