package com.rtech.gpgram.managers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.constants.ApiEndPoints;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

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
                        callback.onError(anError.getMessage());

                    }
                });


    }

}
