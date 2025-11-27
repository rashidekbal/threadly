package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.network_managers.PostsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExplorePostsViewModel extends AndroidViewModel {
    PostsManager postsManager;
    public ExplorePostsViewModel(@NonNull Application application) {
        super(application);
        this.postsManager=new PostsManager();

    }
   private final MutableLiveData<ArrayList<Posts_Model>> posts=new MutableLiveData<>();
    public LiveData<ArrayList<Posts_Model>> getExploreFeed(){
        if(posts.getValue()==null||posts.getValue().isEmpty()){
            loadExploreFeed();
        }
        return posts;
    }

    public void loadExploreFeed() {
        ArrayList<Posts_Model> temp=new ArrayList<>();
        postsManager.getVideoFeed(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray data=response.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        JSONObject postObject=data.getJSONObject(i);
                        temp.add(new Posts_Model(0,
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
                                ,postObject.getString("type").equals("video"),
                                postObject.getInt("isFollowed")>0
                        ));


                    }
                    posts.postValue(temp);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onError(String err) {
                posts.postValue(temp);

            }
        });


    }
}
