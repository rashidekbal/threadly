package com.rtech.threadly.viewmodels;

import android.app.Application;

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
                ArrayList<UsersModel> tempAccountList=new ArrayList<>();
                try {
                    JSONObject data=response.getJSONObject("data");
                    JSONArray accounts=data.getJSONArray("Account");
                    for(int i=0;i<accounts.length();i++){
                        JSONObject account=accounts.getJSONObject(i);
                        tempAccountList.add(new UsersModel(account.getString("uuid"),
                                account.getString("username"),
                                account.getString("userid"),
                                account.getString("profilepic")));

                    }
                    AccountsResult.postValue(tempAccountList);

                } catch (JSONException e) {
                    AccountsResult.postValue(tempAccountList);
                }

            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }
}
