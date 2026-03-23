package com.rtech.threadly.network_managers;

import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;

import java.io.File;

public class StoriesManager {
    SharedPreferences loginInfo;
    public StoriesManager(){
        this.loginInfo= Core.getPreference();
    }

    public void AddStory(File media,String Type, NetworkCallbackInterfaceWithProgressTracking callbackInterface){
        String Url= ApiEndPoints.ADD_STORY;
        NetworkingProvider.uploadWithType(Url, PreferenceUtil.getJWT(),media,"image",Type,"uploadProfile",callbackInterface);

    }
    public void getStories(NetworkCallbackInterfaceJsonObject callback){
        String Url=ApiEndPoints.GET_STORIES;
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callback);

    }
    public void getStoriesOf(String Userid,NetworkCallbackInterfaceJsonObject callbackInterfaceWithJsonObjectDelivery){
        String Url=ApiEndPoints.GET_STORIES+Userid;
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callbackInterfaceWithJsonObjectDelivery);



    }
    public void getMyStories(NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){
        String Url=ApiEndPoints.GET_MY_STORIES;
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callbackInterfaceJsonObject);


    }
    public void RemoveStory(int storyId,NetworkCallbackInterfaceJsonObject callbackInterface){
        String URL=ApiEndPoints.DELETE_STORY+(storyId);
        NetworkingProvider.delete(URL,PreferenceUtil.getJWT(),callbackInterface);

    }
}
