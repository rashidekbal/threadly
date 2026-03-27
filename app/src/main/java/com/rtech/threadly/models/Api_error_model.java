package com.rtech.threadly.models;

import org.json.JSONObject;

public class Api_error_model {
    private final int errorCode;
    private final String errorType;
    private JSONObject errorBody;

    public Api_error_model(JSONObject errorObject){
        this.errorCode=errorBody.optInt("status");
        this.errorType=errorObject.optString("errorType");
        this.errorBody=errorObject.optJSONObject("errorBody");
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public JSONObject getErrorBody() {
        return errorBody;
    }
}
