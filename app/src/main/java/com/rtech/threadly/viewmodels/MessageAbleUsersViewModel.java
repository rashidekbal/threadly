package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.FollowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageAbleUsersViewModel extends AndroidViewModel {
    public MessageAbleUsersViewModel(@NonNull Application application) {
        super(application);
    }
    MutableLiveData<ArrayList<UsersModel>> mutableLiveDataUsers=new MutableLiveData<>();
    public LiveData<ArrayList<UsersModel>> getUsersList() {
        if(mutableLiveDataUsers.getValue()==null||mutableLiveDataUsers.getValue().isEmpty()){
            loadUsers();
        }
        return mutableLiveDataUsers;}

    private void loadUsers() {
        FollowManager followManager=new FollowManager();
        followManager.getFollowings(Core.getPreference().getString(SharedPreferencesKeys.USER_ID, null), new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray list=response.getJSONArray("data");
                    if(list.length()>0){

                        ArrayList <UsersModel> array=new ArrayList<>();
                        for(int i=0;i<list.length();i++){

                            JSONObject object=list.getJSONObject(i);
                            array.add(new UsersModel(object.getString("uuid"),
                                    object.getString("username"),
                                    object.getString("userid"),
                                    object.getString("profilepic")));

                        }
                        mutableLiveDataUsers.postValue(array);

                    }else{
                        mutableLiveDataUsers.postValue(new ArrayList<>());
                    }
                } catch (JSONException e) {

                    mutableLiveDataUsers.postValue(new ArrayList<>());
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                mutableLiveDataUsers.postValue(new ArrayList<>());

            }
        });
    }
}
