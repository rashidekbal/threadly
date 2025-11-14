package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.network_managers.ProfileManager;
import com.rtech.threadly.models.Preview_Post_model;
import com.rtech.threadly.models.Profile_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileViewModel extends AndroidViewModel {
    // ViewModel for managing profile-related data and operations
    ProfileManager profileManager;
    PostsManager postsManager;
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileManager=new ProfileManager();
        this.postsManager=new PostsManager();

    }


    MutableLiveData<Profile_Model> profileLiveData = new MutableLiveData<>();

    public LiveData<Profile_Model> getProfileLiveData(){
        if(profileLiveData.getValue()==null){
            loadProfile();
        }
        return profileLiveData;
    }
    public void loadProfile() {
//        Log.d("errorLoading", "startLoading: ");
        profileManager.getLoggedInUserProfile(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                Profile_Model userdata;
                try {
                    JSONArray array= response.getJSONArray("data");
                    JSONObject object=array.getJSONObject(0);
                    userdata=new Profile_Model(
                            object.getString("userid")
                            ,object.getString("username")
                            ,object.getString("profilepic")
                            ,object.getString("bio")
                            ,object.getString("dob").split("T")[0]
                            ,object.getInt("followersCount")
                            ,object.getInt("followingCount")
                            ,object.getInt("PostsCount")
                            ,0,
                            0,
                            object.getInt("isPrivate")==1,
                            true);
                    profileLiveData.postValue(userdata);
                } catch (JSONException e) {
                    profileLiveData.postValue(null);
                }
            }

            @Override
            public void onError(String err) {
              profileLiveData.postValue(null);

            }
        });
    }



    //    logged in users posts
    MutableLiveData<ArrayList<Posts_Model>> UserPostsLiveData = new MutableLiveData<>();

    public LiveData<ArrayList<Posts_Model>> getUserPostsLiveData() {
        if (UserPostsLiveData.getValue() == null || UserPostsLiveData.getValue().isEmpty()) {
            loadLoggedInUserPosts();
        }
        return UserPostsLiveData ;    }

    public void loadLoggedInUserPosts() {
        postsManager.getLoggedInUserPost(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                ArrayList<Posts_Model> tempArrayList=new ArrayList<>();
                try {
                    JSONArray data=response.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        JSONObject object= data.getJSONObject(i);
                        tempArrayList.add(new Posts_Model(0,
                                object.getInt("postid"),
                                object.getString("userid"),
                                object.getString("username"),
                                object.getString("profilepic"),
                                object.getString("imageurl"),
                                object.getString("caption"),
                                object.getString("created_at"),
                                object.getString("likedBy"),
                                object.getInt("likeCount"),
                                object.getInt("commentCount"),
                                object.getInt("shareCount"),
                                object.getInt("isLiked")
                                ,object.getString("type").equals("video"),
                                object.getInt("isFollowed")>0
                        ));
                    }
                    UserPostsLiveData.postValue(tempArrayList);
                } catch (JSONException e) {
                    UserPostsLiveData.postValue(new ArrayList<>());
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onError(String err) {
                UserPostsLiveData.postValue(new ArrayList<>());

            }
        });
    }

}
