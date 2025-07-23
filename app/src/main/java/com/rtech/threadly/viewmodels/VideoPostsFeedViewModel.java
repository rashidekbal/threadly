package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.managers.PostsManager;
import com.rtech.threadly.models.Posts_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoPostsFeedViewModel extends AndroidViewModel {
    PostsManager postsManager=new PostsManager();
    public VideoPostsFeedViewModel(@NonNull Application application) {
        super(application);
    }
    MutableLiveData<ArrayList<Posts_Model>> MutableLiveVideoPostData=new MutableLiveData<>();
    public LiveData<ArrayList<Posts_Model>> getLiveVideoPostsFeed(){
        if(MutableLiveVideoPostData.getValue()==null||MutableLiveVideoPostData.getValue().isEmpty()){
            loadVideoPostFeed();
        }
        return MutableLiveVideoPostData;
    }

    public  void loadVideoPostFeed() {
        postsManager.getVideoFeed(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                ArrayList<Posts_Model> tempArrayList = new ArrayList<>();
                try {
                    JSONArray data=response.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        JSONObject postObject=data.getJSONObject(i);
                        tempArrayList.add(new Posts_Model(
                                postObject.getInt("postid"),
                                postObject.getString("userid"),
                                postObject.getString("username"),
                                postObject.getString("profilepic"),
                                postObject.getString("imageurl"),
                                postObject.getString("caption"),
                                postObject.getString("created_at"),
                                postObject.getString("likedBy"),
                                postObject.getInt("likeCount"),
                                postObject.getInt("commentCount"),
                                postObject.getInt("shareCount"),
                                postObject.getInt("isLiked")
                                ,postObject.getString("type").equals("video")
                        ));


                    }
                    MutableLiveVideoPostData.postValue(tempArrayList);
                } catch (JSONException e) {
//                        Log.d("dataloadException", "onSuccess: "+e.
//                                toString());
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                MutableLiveVideoPostData.postValue(new ArrayList<>());

            }
        });

    }

}
