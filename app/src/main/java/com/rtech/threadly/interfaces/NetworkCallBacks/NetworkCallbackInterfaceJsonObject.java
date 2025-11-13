package com.rtech.threadly.interfaces.NetworkCallBacks;

import org.json.JSONObject;

public interface NetworkCallbackInterfaceJsonObject {
    void onSuccess(JSONObject response);
    void onError(int errorCode);
}
