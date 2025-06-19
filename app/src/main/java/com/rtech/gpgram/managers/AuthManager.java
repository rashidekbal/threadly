package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.rtech.gpgram.constants.ApiEndPoints;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;

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
    public void Login(String userid, String password, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        // Implement the login logic here
        // This could involve making a network request to your server with the provided userid and password
        // Once the response is received, you can call the callback methods to deliver the result

        // Example:
        // AndroidNetworking.post("YOUR_LOGIN_URL")
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
        //                 callback.onError(error);
        //             }
        //         });
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
                            callback.onError(anError.getMessage());
                        }
                    });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }


    }
}
