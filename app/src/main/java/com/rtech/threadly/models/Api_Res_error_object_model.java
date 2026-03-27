package com.rtech.threadly.models;

import org.json.JSONObject;

public class Api_Res_error_object_model {
    private final  String errorDetails;

    public Api_Res_error_object_model(JSONObject errObject) {
        this.errorDetails = errObject.optString("errorDetails");
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
