package com.rtech.gpgram.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.managers.PostsManager;
import com.rtech.gpgram.models.Posts_Model;
import com.rtech.gpgram.models.Preview_Post_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostsViewModel extends AndroidViewModel {
    //creation of PostsManager
    //to manage posts related operations
    PostsManager postsManager;
    public PostsViewModel(@NonNull Application application) {
        super(application);
        this.postsManager=new PostsManager(application);

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
            postsManager.getFeed(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                @Override
                public void onSuccess(JSONObject response) {
//                    Log.d("dataloadException", "data Loading sucess: "+response.toString());
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
                            ));


                        }
                        postsLiveData.postValue(tempArrayList);
                    } catch (JSONException e) {
//                        Log.d("dataloadException", "onSuccess: "+e.
//                                toString());
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onError(String err) {
//                    Log.d("PostsViewModelDataLoadError", err);
                    postsLiveData.postValue(new ArrayList<>());


                }
            });

    }






}
