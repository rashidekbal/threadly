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
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ProfileEditorManager {

    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;

    public ProfileEditorManager() {

        this.loginInfo = Core.getPreference();
        this.editor= loginInfo.edit();


    }
    private String getToken(){
        return loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }
    public void UpdateName(String name, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface){
        String url= ApiEndPoints.EDIT_USERNAME;
        JSONObject packet=new JSONObject();

        try {
            packet.put("name",name);
            AndroidNetworking.patch(url)
                    .setPriority(Priority.HIGH)
                    .addHeaders("Authorization", "Bearer "+getToken())
                    .addApplicationJsonBody(packet)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "error on updating name "+anError.toString());
                            }

                            callbackInterface.onError(anError.getMessage());

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void UpdateUserid(String userid, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface){
        String url= ApiEndPoints.EDIT_USERID;
        JSONObject packet=new JSONObject();

        try {
            packet.put("newUserId",userid);
            AndroidNetworking.patch(url)
                    .setPriority(Priority.HIGH)
                    .addHeaders("Authorization", "Bearer "+getToken())
                    .addApplicationJsonBody(packet)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "error on updating userid "+anError.toString());
                            }

                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }


    public void UpdateUserBio(String BioText, NetworkCallbackInterface callbackInterface){
        String url= ApiEndPoints.EDIT_BIO;
        JSONObject packet=new JSONObject();

        try {
            packet.put("bioText",BioText);
            AndroidNetworking.patch(url)
                    .setPriority(Priority.HIGH)
                    .addHeaders("Authorization", "Bearer "+getToken())
                    .addApplicationJsonBody(packet)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess();
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "error on updating user bio"+anError.toString());
                            }

                            callbackInterface.onError(anError.getMessage());

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public final void ChangeUserProfile(File picture,NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.EDIT_PROFILE_PICTURE;
        AndroidNetworking.upload(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+getToken())
                .addMultipartFile("image",picture)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
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





    public final void updatePreferences(String token,String userid) {

        editor.putString(SharedPreferencesKeys.JWT_TOKEN, token);
        editor.putString(SharedPreferencesKeys.USER_ID, userid);
        editor.apply();
    }
    public final void updatePreferences(String username) {
        editor.putString(SharedPreferencesKeys.USER_NAME, username);
        editor.apply();
    }
    public final void updateUserProfile(String profilePic) {
        editor.putString(SharedPreferencesKeys.USER_PROFILE_PIC, profilePic);
        editor.apply();
    }
}
