package com.rtech.threadly.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.managers.StoriesManager;
import com.rtech.threadly.models.StoriesModel;

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

    private void loadStories() {
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

}
