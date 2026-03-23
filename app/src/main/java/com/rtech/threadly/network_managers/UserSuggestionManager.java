package com.rtech.threadly.network_managers;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;

public class UserSuggestionManager {

    public static void getSuggestedUsers(NetworkCallbackInterfaceJsonObject callbackInterface){
        NetworkingProvider.get(ApiEndPoints.GET_SUGGESTED_USERS, PreferenceUtil.getJWT(),callbackInterface);


    }
}
