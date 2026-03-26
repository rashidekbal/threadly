package com.rtech.threadly.interfaces;

import org.json.JSONObject;

public interface NetworkCallbackInterfaceWithProgressTracking {
    void onSuccess(JSONObject response);
    void onError(int errorCode , JSONObject errorObject);
    void progress(long bytesUploaded, long totalBytes);
}
