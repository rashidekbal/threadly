package com.rtech.threadly.network_managers;


import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;
public class FollowManager {


    public FollowManager(){

    }

    public void  follow(String userid, NetworkCallbackInterfaceJsonObject callbackInterface) {
        String url=ApiEndPoints.FOLLOW_V2;

        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);
            NetworkingProvider.post(url, PreferenceUtil.getJWT(),packet,callbackInterface);



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public void cancelFollowRequest(String userid,NetworkCallbackInterfaceJsonObject callbackInterface){
        String url=ApiEndPoints.CANCEL_FOLLOW_REQUEST;
        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);
            NetworkingProvider.post(url,PreferenceUtil.getJWT(),packet,callbackInterface);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void unfollow(String userid, NetworkCallbackInterfaceJsonObject callbackInterface) {
        String url=ApiEndPoints.UNFOLLOW;
        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);
            NetworkingProvider.post(url,PreferenceUtil.getJWT(),packet,callbackInterface);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void getFollowers(String userid, NetworkCallbackInterfaceJsonObject callback){
        String url=ApiEndPoints.GET_FOLLOWERS.concat(userid);
        NetworkingProvider.get(url,PreferenceUtil.getJWT(),callback);
    }
    public  void getFollowings(String userid, NetworkCallbackInterfaceJsonObject callback){
        String url=ApiEndPoints.GET_FOLLOWINGS.concat(userid);
        NetworkingProvider.get(url,PreferenceUtil.getJWT(),callback);

    }
    public static void approveFollowRequest(String followerId,NetworkCallbackInterfaceJsonObject callBack){
        String Url=ApiEndPoints.ACCEPT_FOLLOW_REQUEST;
        JSONObject packet = new JSONObject();
        try {
            packet.put("followerId",followerId);
            NetworkingProvider.post(Url,PreferenceUtil.getJWT(),packet,callBack);

        } catch (JSONException e) {
            callBack.onError(500, new JSONObject());
        }
    }
    public static void getAllFollowRequests(NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){
        String Url=ApiEndPoints.GET_ALL_FOLLOW_REQUEST;
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callbackInterfaceJsonObject);

    }
    public static void rejectFollowRequest(String followerId,NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url= ApiEndPoints.REJECT_FOLLOW_REQUEST+followerId;
        NetworkingProvider.delete(Url,PreferenceUtil.getJWT(),callbackInterface);

    }
}
