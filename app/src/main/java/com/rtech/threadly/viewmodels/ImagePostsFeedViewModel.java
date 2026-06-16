package com.rtech.threadly.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ImagePostsFeedViewModel extends AndroidViewModel {
    //creation of PostsManager
    //to manage posts related operations
    PostsManager postsManager;
    private int page;
    private int SEED;
    boolean loading=true;

    public ImagePostsFeedViewModel(@NonNull Application application) {
        super(application);
        this.postsManager=new PostsManager();
        freshFeedState();
        page=1;

    }


   final MutableLiveData<ArrayList<Posts_Model>> postsLiveData=new MutableLiveData<>();

    public LiveData<ArrayList<Posts_Model>> getPostsLiveData(){
        if (postsLiveData.getValue() == null || postsLiveData.getValue().isEmpty()) {
            loadFeedPosts();
        }

        return postsLiveData;
    }
    public void loadFeedPosts() {
        loading=true;
        try {
            postsManager.getImageFeedV2(page,SEED,new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    loading=false;
                    ArrayList<Posts_Model> tempArrayList = new ArrayList<>();
                    try {
                        JSONArray data=response.getJSONArray("data");
                        if (data.length()<1)return;
                        for(int i=0;i<data.length();i++){
                            JSONObject postObject=data.getJSONObject(i);
                            tempArrayList.add(new Posts_Model(0,
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
                                    ,postObject.optInt("isFollowed")>0,
                                    false,
                                    postObject.optInt("viewCount")
                            ));


                        }
                        int size=tempArrayList.size();
                        insertSuggestionAtRandom(size,tempArrayList);
                    } catch (JSONException e) {
                        loading=false;
                        LoggerUtil.writeToFile(e.toString(),"jsonException"+new Date()+".txt");
                        postsLiveData.postValue(new ArrayList<>());
                    }

                }

                @Override
                public void onError(int err, JSONObject errorObject) {
                    loading=false;
                    postsLiveData.postValue(new ArrayList<>());
                    LoggerUtil.writeToFile(errorObject.toString(),"ImageFeedViewModel"+new Date()+".txt");


                }
            });
        } catch (JSONException e) {
            loading=false;
            postsLiveData.postValue(new ArrayList<>());
            LoggerUtil.writeToFile(e.toString(),"ImageFeedViewModel"+new Date()+".txt");
        }

    }
    private void insertSuggestionAtRandom(int size,ArrayList<Posts_Model> postsModels){
        int timesOfInsertion=(int)Math.ceil((float)size/100f);
        for(int i=0;i<timesOfInsertion;i++) {

            int randomPosition = (int) Math.floor(Math.random() * size);
            while(randomPosition==0){
                randomPosition = (int) Math.floor(Math.random() * size);
            }
            postsModels.add(randomPosition,new Posts_Model(1,0,"","","","","","","",0,0,0,0,false,false,false,0));

        }
        postsLiveData.postValue(postsModels);
    }
    public void freshFeedState() {
        SEED=(int)Math.floor(Math.random()*999999);
        page=1;

    }
    public void loadMore(){

        if(loading)return;
        page++;

        loadFeedPosts();

    }






}
