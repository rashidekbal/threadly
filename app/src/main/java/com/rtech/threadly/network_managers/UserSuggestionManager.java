package com.rtech.threadly.network_managers;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.Profile_Model_minimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserSuggestionManager {

    public static void getSuggestedUsers(NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface){
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("/users/getUsers"))
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN, "null")))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface.onSuccess(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterface.onError(anError.toString());
//                        Log.d("networkcallException", "onResponse: ".concat(anError.toString()));
                    }
                });

    }
}
