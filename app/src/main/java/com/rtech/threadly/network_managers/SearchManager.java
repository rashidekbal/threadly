package com.rtech.threadly.network_managers;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONObject;

public class SearchManager {
    public static void Search(String query,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){
        String Url= ApiEndPoints.SEARCH+query;
        NetworkingProvider.get(Url, PreferenceUtil.getJWT(),new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                callbackInterfaceJsonObject.onSuccess(response);
            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                callbackInterfaceJsonObject.onError(errorCode, errorObject);
            }
        });

    }
}
