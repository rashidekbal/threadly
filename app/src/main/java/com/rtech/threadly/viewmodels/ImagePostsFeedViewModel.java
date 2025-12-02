package com.rtech.threadly.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.models.Posts_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImagePostsFeedViewModel extends AndroidViewModel {
    //creation of PostsManager
    //to manage posts related operations
    PostsManager postsManager;
    public ImagePostsFeedViewModel(@NonNull Application application) {
        super(application);
        this.postsManager=new PostsManager();

    }

// creation of LiveData object to hold posts data
   final MutableLiveData<ArrayList<Posts_Model>> postsLiveData=new MutableLiveData<>();
    // method to get LiveData object
    public LiveData<ArrayList<Posts_Model>> getPostsLiveData(){
//        Log.d("dataloadException", "called for data: ");
        if (postsLiveData.getValue() == null || postsLiveData.getValue().isEmpty()) {
//            Log.d("dataloadException", "going to load data: ");

            loadFeedPosts();
        }
//        Log.d("dataloadException", "returning data ");
        return postsLiveData;
    }
    // method to load posts from server
    public void loadFeedPosts() {
        Log.d("dataloadException", "data Loading started: ");
            postsManager.getImageFeed(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                @Override
                public void onSuccess(JSONObject response) {
//                    Log.d("dataloadException", "data Loading sucess: "+response.toString());
                    ArrayList<Posts_Model> tempArrayList = new ArrayList<>();
                    try {
                        JSONArray data=response.getJSONArray("data");
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
                                    ,postObject.optInt("isFollowed")>0
                            ));


                        }
                        int size=tempArrayList.size();
                        insertSuggestionAtRandom(size,tempArrayList);
                    } catch (JSONException e) {
                        postsLiveData.postValue(new ArrayList<>());
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onError(String err) {
                    postsLiveData.postValue(new ArrayList<>());


                }
            });

    }
    private void insertSuggestionAtRandom(int size,ArrayList<Posts_Model> postsModels){
        int timesOfInsertion=(int)Math.ceil((float)size/100f);
        for(int i=0;i<timesOfInsertion;i++) {

            int randomPosition = (int) Math.floor(Math.random() * size);
            while(randomPosition==0){
                randomPosition = (int) Math.floor(Math.random() * size);
            }

            Log.d("suggestionInsertedAt", "insertSuggestionAtRandom: "+ randomPosition);
            postsModels.add(randomPosition,new Posts_Model(1,0,"","","","","","","",0,0,0,0,false,false));

        }
        postsLiveData.postValue(postsModels);
    }






}
