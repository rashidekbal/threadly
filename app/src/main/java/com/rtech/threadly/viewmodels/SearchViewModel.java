package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.UsersModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchViewModel extends AndroidViewModel {
    public SearchViewModel(@NonNull Application application) {
        super(application);
    }
    MutableLiveData<ArrayList<UsersModel>> AccountsResult=new MutableLiveData<>();
    MutableLiveData<ArrayList<Posts_Model>> postsResult=new MutableLiveData<>();
    public MutableLiveData<ArrayList<UsersModel>> getAccountsResult() {
        return AccountsResult;
    }

    public MutableLiveData<ArrayList<Posts_Model>> getPostsResult() {
        return postsResult;
    }
}
