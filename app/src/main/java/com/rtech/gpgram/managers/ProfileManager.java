package com.rtech.gpgram.managers;

import static android.content.Context.MODE_PRIVATE;

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
import com.rtech.gpgram.models.Profile_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileManager {
    Context context;
    SharedPreferences loginInfo;
    String token;
    public ProfileManager(Context c){
        this.context=c;
        this.loginInfo=c.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,MODE_PRIVATE);
        AndroidNetworking.initialize(context);
        this.token=loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");

    }

    public final void GetProfile(String Userid, NetworkCallbackInterfaceWithJsonObjectDelivery callback){
        String url= ApiEndPoints.GET_PROFILE.concat(Userid);
        AndroidNetworking.get(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer "+token)
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
                        callback.onError(anError.getMessage());

                    }
                });


    }
    public final void getLoggedInUserProfile(NetworkCallbackInterfaceWithJsonObjectDelivery callback){

        String url=ApiEndPoints.GET_LOGGED_IN_USER_PROFILE;
        AndroidNetworking.get(url)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization", "Bearer "+loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener(){

                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                    @Override
                    public void onError(ANError anError) {
                        if(BuildConfig.DEBUG){
                            Log.d("ApiError", "Error"+ anError.getMessage());

                        }
                        callback.onError(anError.getMessage());

                    }
                });

    }

}
