package com.rtech.threadly.models;

import org.json.JSONObject;

public class Api_Res_AccountRestriction_object_model {
    private final String type;
    private final String reason;
    private final String banTime;

    public Api_Res_AccountRestriction_object_model(JSONObject errorBody) {
       this.type=errorBody.optString("type");
        this.reason=errorBody.optString("reason");
        this.banTime=errorBody.optString("banTime");
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public String getBanTime() {
        return banTime;
    }
}
