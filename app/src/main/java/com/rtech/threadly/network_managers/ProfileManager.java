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
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;

public class ProfileManager {
    SharedPreferences loginInfo;

    public ProfileManager(){
        this.loginInfo= Core.getPreference();
    }
    private String getToken(){
       return PreferenceUtil.getJWT();
    }

    public final void GetProfile(String Userid, NetworkCallbackInterfaceJsonObject callback){
        String url= ApiEndPoints.GET_PROFILE.concat(Userid);
        NetworkingProvider.get(url,getToken(),callback);



    }
    public final void GetProfileByUuid(String uuid, NetworkCallbackInterfaceJsonObject callback){
        String url= ApiEndPoints.GET_PROFILE_BY_UUID.concat(uuid);
        NetworkingProvider.get(url,getToken(),callback);






    }
    public final void getLoggedInUserProfile(NetworkCallbackInterfaceJsonObject callback){

        String url=ApiEndPoints.GET_LOGGED_IN_USER_PROFILE;
        NetworkingProvider.get(url,getToken(),callback);


    }

}
