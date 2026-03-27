package com.rtech.threadly.network_managers;


import android.util.Log;

import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;
import java.io.File;

public class PostsManager {

    private String getToken(){
        return PreferenceUtil.getJWT();
    }
    public void uploadImagePost(File imagefile,String caption, NetworkCallbackInterfaceWithProgressTracking callback){
        String url=ApiEndPoints.ADD_IMAGE_POST;
        NetworkingProvider.upload(url, PreferenceUtil.getJWT(),imagefile,"image",caption,"postUpload",callback);

    }
    public void uploadVideoPost(File videofile,String caption, NetworkCallbackInterfaceWithProgressTracking callback){
        String url=ApiEndPoints.ADD_VIDEO_POST;
        NetworkingProvider.upload(url, PreferenceUtil.getJWT(),videofile,"video",caption,"postUpload",callback);
    }

    public void getPostWithId(int postId, NetworkCallbackInterfaceJsonObject callbackInterface){


        String url= ApiEndPoints.GET_POST_BY_ID.concat(Integer.toString(postId));
        NetworkingProvider.get(url,getToken(),callbackInterface);

    }
    public void getUserPosts(int page,String userId, NetworkCallbackInterfaceJsonObject callbackInterface)
    {

        String url=ApiEndPoints.GET_USER_POSTS.concat(userId)+"?page="+page;
        NetworkingProvider.get(url,getToken(),callbackInterface);
    }
    public void getImageFeed(NetworkCallbackInterfaceJsonObject callback){

        String url=ApiEndPoints.GET_IMAGE_FEED;
        NetworkingProvider.get(url,getToken(),callback);

    }
    public void getVideoFeed(NetworkCallbackInterfaceJsonObject callback){

        if(BuildConfig.DEBUG){
            Log.d("ApiData", "loading started");

        }

        String url=ApiEndPoints.GET_VIDEO_FEED;
        NetworkingProvider.get(url,getToken(),callback);

    }

    public void getLoggedInUserPost(int page,NetworkCallbackInterfaceJsonObject callbackInterface)
    {
        String url=ApiEndPoints.GET_USER_POSTS.concat(PreferenceUtil.getUserId())+"?page="+page;
        NetworkingProvider.get(url,getToken(),callbackInterface);


    }
    public void RemovePost(int postId, NetworkCallbackInterfaceJsonObject callbackInterface){
        String URL=ApiEndPoints.DELETE_POST+Integer.toString(postId);
        NetworkingProvider.delete(URL,getToken(),callbackInterface);



    }
    public static void markPostViewed(int postId,JSONObject object,NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url=ApiEndPoints.POST_VIEWED+postId;

            NetworkingProvider.post(Url,PreferenceUtil.getJWT(),object,callbackInterface);



    }

}
