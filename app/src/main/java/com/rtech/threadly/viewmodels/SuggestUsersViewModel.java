package com.rtech.threadly.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.network_managers.UserSuggestionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuggestUsersViewModel extends AndroidViewModel {
    public SuggestUsersViewModel(@NonNull Application application) {
        super(application);
    }
    MutableLiveData<ArrayList<Profile_Model_minimal>> profileModelMutableLiveData=new MutableLiveData<>();

    public LiveData<ArrayList<Profile_Model_minimal>> getSuggestedUsers(){
        if(profileModelMutableLiveData.getValue()==null||profileModelMutableLiveData.getValue().isEmpty()){
             loadSuggestedUsers();

        }
        return profileModelMutableLiveData;
    }

    private void loadSuggestedUsers() {
        UserSuggestionManager.getSuggestedUsers(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                ArrayList<Profile_Model_minimal> suggestUsersList = new ArrayList<>();

                                        try {
                            JSONArray data = response.getJSONArray("data");
                            if(data.length()>0){
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject individualUser = data.getJSONObject(i);
                                suggestUsersList.add(new Profile_Model_minimal(
                                        individualUser.getString("userid"),
                                        individualUser.getString("username"),
                                        individualUser.getString("profilepic"),
                                        individualUser.getInt("isfollowedBy")
                                ));
                            }
                            profileModelMutableLiveData.postValue(suggestUsersList);

                            }else{
                                profileModelMutableLiveData.postValue(new ArrayList<>());

                            }
                        } catch (JSONException e) {
                            Log.d("jsonException", "onResponse: ".concat(e.toString()));
                        }
            }

            @Override
            public void onError(String err) {
                profileModelMutableLiveData.postValue(new ArrayList<>());


            }
        });
    }
}
