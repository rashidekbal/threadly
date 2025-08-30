package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.StoriesManager;
import com.rtech.threadly.models.StoriesModel;
import com.rtech.threadly.models.StoryMediaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoriesViewModel extends AndroidViewModel {
    StoriesManager storiesManager;
    public StoriesViewModel(@NonNull Application application) {
        super(application);
        storiesManager=new StoriesManager();
    }
    MutableLiveData<ArrayList<StoriesModel>> mutableLiveData=new MutableLiveData<>();

    public LiveData<ArrayList<StoriesModel>> getStories(){
        if(mutableLiveData.getValue()==null){
            loadStories();
        }

        return mutableLiveData;
    }

    public void loadStories() {
        ArrayList<StoriesModel> arrayList=new ArrayList<>();
        storiesManager.getStories(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray data=response.getJSONArray("data");
                    for(int i=0; i<data.length();i++){
                        JSONObject object=data.getJSONObject(i);
                        arrayList.add(new StoriesModel(object.getString("userid"),
                                object.getString("profilepic"),
                                false));

                    }
                    mutableLiveData.postValue(arrayList);

                } catch (JSONException e) {
                    mutableLiveData.postValue(new ArrayList<>());
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String err) {
                mutableLiveData.postValue(new ArrayList<>());

            }
        });

    }


    MutableLiveData<ArrayList<StoryMediaModel>> mutableStoryMediaModelData=new MutableLiveData<>();

    public LiveData<ArrayList<StoryMediaModel>> getMyStories(){
        if(mutableStoryMediaModelData.getValue()==null){
            loadMyStories();

        }
        return mutableStoryMediaModelData;
    }
    public void loadMyStories(){
        ArrayList<StoryMediaModel> arrayList=new ArrayList<>();
        storiesManager.getMyStories( new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray array=response.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        JSONObject object =array.getJSONObject(i);
                        arrayList.add(new StoryMediaModel(
                                object.getString("userid"),
                                object.getInt("id"),
                                object.getString("storyUrl"),
                                object.getString("type"),
                                object.getString("createdAt"),
                                object.getInt("isLiked")
                                ));

                    }
                    mutableStoryMediaModelData.postValue(arrayList);

                } catch (JSONException e) {
                    mutableStoryMediaModelData.postValue(new ArrayList<>());
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                mutableStoryMediaModelData.postValue(new ArrayList<>());

            }
        });

    }

}
