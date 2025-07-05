package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.constants.ApiEndPoints;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthManager {
    Context context;
    SharedPreferences loginInfo;
    public AuthManager(Context c){
        // Initialize any necessary components or configurations for authentication
        this.context=c;
        loginInfo = context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        AndroidNetworking.initialize(context);

    }
    public void Register(String userid, String password, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        // Implement the registration logic here
        // This could involve making a network request to your server with the provided userid and password
        // Once the response is received, you can call the callback methods to deliver the result

        // Example:
        // AndroidNetworking.post("YOUR_REGISTER_URL")
        //         .addBodyParameter("userid", userid)
        //         .addBodyParameter("password", password)
        //         .build()
        //         .getAsJSONObject(new JSONObjectRequestListener() {
        //             @Override
        //             public void onResponse(JSONObject response) {
        //                 callback.onSuccess(response);
        //             }
        //
        //             @Override
        //             public void onError(ANError error) {
        //                 callback.onError(error.getMessage());
        //             }
        //         });
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
    public void Logout(NetworkCallbackInterface callbackIterface){
        // Implement the logout logic here
        // This could involve clearing the user's session, removing tokens, etc.
        // Once the logout is successful, you can call the callback methods to notify the result

        // Example:
        SharedPreferences.Editor editor = loginInfo.edit();
        editor.clear(); // Clear all stored login information
        editor.apply(); // Apply changes

        callbackIterface.onSuccess(); // Notify success
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

}
