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
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentsManager {

    public CommentsManager(){


    }

    public void getCommentOf(int postId, NetworkCallbackInterfaceJsonObject callback) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url =ApiEndPoints.GET_COMMENTS.concat(Integer.toString(postId));
        NetworkingProvider.get(url,PreferenceUtil.getJWT(),callback );

    }
    public void addComment(int postId, String comment, NetworkCallbackInterfaceJsonObject callback){
        String url = ApiEndPoints.ADD_COMMENT;
        JSONObject data=new JSONObject();
        try {
            data.put("postid", postId);
            data.put("comment", comment);
            NetworkingProvider.post(url,PreferenceUtil.getJWT(),data,callback);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void ReplyToComment(int postId, int commentId, String comment, NetworkCallbackInterfaceJsonObject callbackInterface) throws JSONException {
        String Url=ApiEndPoints.REPLY_TO_COMMENT+Integer.toString(commentId);
        JSONObject packet=new JSONObject();
        packet.put("postId",postId);
        packet.put("comment",comment);
        NetworkingProvider.post(Url,PreferenceUtil.getJWT(),packet,callbackInterface);


    }
    public void GetCommentReplies(int commentId,NetworkCallbackInterfaceJsonObject callback){
        String Url=ApiEndPoints.GET_COMMENT_REPLIES+(commentId);
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callback);
    }
}
