package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ExplorePostsViewModel extends AndroidViewModel {
    PostsManager postsManager;
    private int page;
    private  int SEED;
    public ExplorePostsViewModel(@NonNull Application application) {
        super(application);
        this.postsManager=new PostsManager();
        freshFeed();

    }

    public  void freshFeed() {
        SEED=(int)Math.floor(Math.random()*999999);
        page=1;
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
        try {
            postsManager.getVideoFeed(page,SEED,new NetworkCallbackInterfaceJsonObject() {
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
                                    postObject.getInt("isFollowed")>0,
                                    false,
                                    postObject.getInt("viewCount")
                            ));


                        }
                        posts.postValue(temp);
                    } catch (JSONException e) {
                        posts.postValue(new ArrayList<>());

                    }


                }

                @Override
                public void onError(int err, JSONObject errorObject) {
                    posts.postValue(new ArrayList<>());

                }
            });
        } catch (JSONException e) {
            LoggerUtil.writeToFile(e.toString(),"jsonException"+new Date()+".txt");
            ReUsableFunctions.ShowToast("error loading feed");
        }


    }
}
