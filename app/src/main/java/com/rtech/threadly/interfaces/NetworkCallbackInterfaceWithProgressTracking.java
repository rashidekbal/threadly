package com.rtech.threadly.interfaces;

import org.json.JSONObject;

public interface NetworkCallbackInterfaceWithProgressTracking {
    void onSuccess(JSONObject response);
    void onError(String err);
    void progress(long bytesUploaded, long totalBytes);
}
