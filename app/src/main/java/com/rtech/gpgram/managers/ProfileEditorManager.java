package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.constants.ApiEndPoints;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileEditorManager {
    Context context;
    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;
    String token;
    public ProfileEditorManager(Context c) {
        this.context = c;
        this.loginInfo = c.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.editor= loginInfo.edit();
        this.token = loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN, "null");
        // Initialize AndroidNetworking with the application context
        AndroidNetworking.initialize(c);
    }
    public void UpdateName(String name, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface){
        String url= ApiEndPoints.EDIT_USERNAME;
        JSONObject packet=new JSONObject();

        try {
            packet.put("name",name);
            AndroidNetworking.patch(url)
                    .setPriority(Priority.HIGH)
                    .addHeaders("Authorization", "Bearer "+token)
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
                    .addHeaders("Authorization", "Bearer "+token)
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
                    .addHeaders("Authorization", "Bearer "+token)
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




    public final void updatePreferences(String token,String userid) {
        this.token = token;
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
