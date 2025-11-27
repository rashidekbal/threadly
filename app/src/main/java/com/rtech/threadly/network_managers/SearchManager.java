package com.rtech.threadly.network_managers;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;

import org.json.JSONObject;

public class SearchManager {
    public static void Search(String query,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){
        String Url= ApiEndPoints.SEARCH+query;
        AndroidNetworkingLayer.get(Url, new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                callbackInterfaceJsonObject.onSuccess(response);
            }

            @Override
            public void onError(int errorCode) {
                callbackInterfaceJsonObject.onError(errorCode);
            }
        });

    }
}
