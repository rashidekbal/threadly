package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.models.Posts_Comments_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsViewModel extends AndroidViewModel {
    CommentsManager commentsManager=new CommentsManager();
    public CommentsViewModel(@NonNull Application application) {
        super(application);
    }
    MutableLiveData<ArrayList<Posts_Comments_Model>> MutableLiveCommentsData=new MutableLiveData<>();

    public LiveData<ArrayList<Posts_Comments_Model>> getLiveCommentsData(int postId){
        if(MutableLiveCommentsData.getValue()==null||MutableLiveCommentsData.getValue().isEmpty()){
            loadComments(postId);
        }
        return MutableLiveCommentsData;
    }

    private void loadComments(int postId) {
        commentsManager.getCommentOf(postId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                ArrayList<Posts_Comments_Model> commentsTemp=new ArrayList<>();
                try {
                    JSONArray data=response.getJSONArray("data");
                    if(data.length()>0){
                        for (int i=0;i<data.length();i++){
                            JSONObject individualComment=data.getJSONObject(i);
                            commentsTemp.add(new Posts_Comments_Model(individualComment.getInt("commentid"),individualComment.getInt("postid"),individualComment.getInt("comment_likes_count"),individualComment.getInt("isLiked"),individualComment.getString("userid"),individualComment.getString("username"),individualComment.getString("profilepic"),individualComment.getString("comment_text"),individualComment.getString("createdAt")));
                        }
                        MutableLiveCommentsData.postValue(commentsTemp);

                    }else{
                        MutableLiveCommentsData.postValue(new ArrayList<>());
                    }



                } catch (JSONException e) {
                    MutableLiveCommentsData.postValue(new ArrayList<>());
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onError(String err) {
                MutableLiveCommentsData.postValue(new ArrayList<>());

            }
        });
    }
}
