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
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthManager {
    SharedPreferences loginInfo;
    public AuthManager(){
        // Initialize any necessary components or configurations for authentication
        loginInfo = Core.getPreference();
    }
    public void LoginMobile(String mobile,String password,NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url=ApiEndPoints.LOGIN_MOBILE;
        JSONObject packet=new JSONObject();
        try {
            packet.put("userid",mobile);
            packet.put("password",password);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+anError.getMessage());

                            }
                            callback.onError(Integer.toString(anError.getErrorCode()));
                        }
                    });
        } catch (JSONException e) {

            throw new RuntimeException(e);
        }

    }
    public void ResetPasswordWithMobile(String password,String token, NetworkCallbackInterface callback) {

        String url= ApiEndPoints.RESET_PASSWORD_MOBILE;
        JSONObject data = new JSONObject();
        try {
            data.put("password", password);
            AndroidNetworking.post(url)
                    .setPriority(com.androidnetworking.common.Priority.HIGH)
                    .addApplicationJsonBody(data)
                    .addHeaders("Authorization", "Bearer " + token)
                    .build()
                    .getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(com.androidnetworking.error.ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+anError.getMessage());

                            }
                            callback.onError(anError.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }


    }

    public void ResetPasswordWithEmail(String password,String token, NetworkCallbackInterface callback) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.RESET_PASSWORD_EMAIL;
        JSONObject data = new JSONObject();
        try {
            data.put("password", password);
            AndroidNetworking.post(url)
                    .setPriority(com.androidnetworking.common.Priority.HIGH)
                    .addApplicationJsonBody(data)
                    .addHeaders("Authorization", "Bearer " + token)
                    .build()
                    .getAsJSONObject(new com.androidnetworking.interfaces.JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(com.androidnetworking.error.ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callback.onError(anError.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }


    }
    public void LoginEmail(String email,String password,NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.LOGIN_EMAIL;
        JSONObject packet=new JSONObject();
        try {
            packet.put("userid",email);
            packet.put("password",password);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
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
                           callback.onError(Integer.toString(anError.getErrorCode()));
                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void LoginUserId(String email,String password,NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.LOGIN_USERID;
        JSONObject packet=new JSONObject();
        try {
            packet.put("userid",email);
            packet.put("password",password);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
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
                            callback.onError(Integer.toString(anError.getErrorCode()));
                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public  void logout(NetworkCallbackInterface callbackInterface){
        String url=ApiEndPoints.LOGOUT;
        AndroidNetworking.get(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                       callbackInterface.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterface.onError(anError.getErrorDetail());

                    }
                });
    }

}
