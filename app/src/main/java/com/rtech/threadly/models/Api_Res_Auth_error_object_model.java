package com.rtech.threadly.models;

import org.json.JSONObject;

public class Api_Res_Auth_error_object_model {
    private final String invalid_parameter;
    private final String message;

    public Api_Res_Auth_error_object_model(JSONObject errorBody) {
        this.invalid_parameter=errorBody.optString("invalid_parameter");
        this.message=errorBody.optString("message");
    }

    public String getInvalid_parameter() {
        return invalid_parameter;
    }

    public String getMessage() {
        return message;
    }
}
