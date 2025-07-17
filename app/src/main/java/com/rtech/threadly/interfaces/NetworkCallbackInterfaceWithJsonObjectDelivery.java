package com.rtech.threadly.interfaces;

import org.json.JSONObject;

public interface NetworkCallbackInterfaceWithJsonObjectDelivery {
    void onSuccess(JSONObject response);
    void onError(String err);
}
