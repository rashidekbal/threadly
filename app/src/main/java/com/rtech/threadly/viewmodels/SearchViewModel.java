package com.rtech.threadly.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.SearchManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class SearchViewModel extends AndroidViewModel {
    public SearchViewModel(@NonNull Application application) {
        super(application);
    }
    MutableLiveData<ArrayList<UsersModel>> AccountsResult=new MutableLiveData<>();
    MutableLiveData<ArrayList<Posts_Model>> postsResult=new MutableLiveData<>();
    public LiveData<ArrayList<UsersModel>> getAccountsResult() {
        return AccountsResult;
    }

    public LiveData<ArrayList<Posts_Model>> getPostsResult() {
        return postsResult;
    }
    public void search(String Query){
        SearchManager.Search(Query,new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {

                try {
                    JSONObject data=response.getJSONObject("data");
                    JSONArray accounts=data.getJSONArray("Account");
                    JSONArray reels=data.getJSONArray("Reels");
                    handleAccountData(accounts);
                    handleReelsData(reels);


                } catch (JSONException e) {
                    AccountsResult.postValue(new ArrayList<>());
                    postsResult.postValue(new ArrayList<>());

                }

            }
            @Override
            public void onError(int errorCode) {
                AccountsResult.postValue(new ArrayList<>());
                postsResult.postValue(new ArrayList<>());
            }
        });
    }

    private void handleReelsData(JSONArray reels) {
        ArrayList<Posts_Model> tempReelsList=new ArrayList<>();
        if(reels.length()==0){
            postsResult.postValue(tempReelsList);
            return;
        }
        try {
            for(int i=0;i<reels.length();i++){
                JSONObject postObject=reels.getJSONObject(i);
                tempReelsList.add(new Posts_Model(0,
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
            postsResult.postValue(tempReelsList);
        } catch (JSONException e) {
            postsResult.postValue(tempReelsList);
        }


    }

    private void handleAccountData(JSONArray accounts) {

        ArrayList<UsersModel> tempAccountList=new ArrayList<>();
        if(accounts.length()==0){
            AccountsResult.postValue(tempAccountList);
            return;
        }
       try {
           for (int i = 0; i < accounts.length(); i++) {
               JSONObject account = accounts.getJSONObject(i);
               tempAccountList.add(new UsersModel(account.getString("uuid"),
                       account.getString("username"),
                       account.getString("userid"),
                       account.getString("profilepic")));

           }
           AccountsResult.postValue(tempAccountList);
       }
       catch (JSONException e){
           AccountsResult.postValue(tempAccountList);
       }


    }
    public void setSearching(){
        AccountsResult.postValue(null);
        postsResult.postValue(null);
    }

}
