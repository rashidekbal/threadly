package com.rtech.threadly.network_managers;

import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;

public class LikeManager {
    public LikeManager(){

    }
    private String getToken(){
        return PreferenceUtil.getJWT();
    }

    public void likePost(int postId, NetworkCallbackInterfaceJsonObject callbackIterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.LIKE_POST.concat(Integer.toString(postId));
        NetworkingProvider.get(url,getToken(),callbackIterface);
}

    public void UnlikePost(int postId, NetworkCallbackInterfaceJsonObject callbackInterface) {

        String url=ApiEndPoints.UNLIKE_POST.concat(Integer.toString(postId));
        NetworkingProvider.get(url, getToken(),callbackInterface);



    }

    public void LikeAComment(int commentId, NetworkCallbackInterfaceJsonObject callbackInterface) {

        String url=ApiEndPoints.LIKE_COMMENT.concat(Integer.toString(commentId));
        NetworkingProvider.get(url,getToken(),callbackInterface);
    }
    public void UnLikeAComment(int commentId, NetworkCallbackInterfaceJsonObject callbackInterface) {

        String url=ApiEndPoints.UNLIKE_COMMENT.concat(Integer.toString(commentId));
        NetworkingProvider.get(url,getToken(),callbackInterface);


    }
    public void LikeStory(int StoryId,NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url=ApiEndPoints.LIKE_STORY+(StoryId);
        NetworkingProvider.get(Url,getToken(),callbackInterface);

    }
    public void UnLikeStory(int StoryId,NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url=ApiEndPoints.UNLIKE_STORY+(StoryId);
        NetworkingProvider.get(Url,getToken(),callbackInterface);

    }




}
