package com.rtech.threadly.network_managers;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class FcmManager {

    public static void UpdateFcmToken(String token, NetworkCallbackInterface callbackInterface){
        String url= ApiEndPoints.FCM_TOKEN_UPDATE;
        JSONObject object=new JSONObject();
        try {
            object.put("token",token);
            AndroidNetworking.patch(url).setPriority(Priority.HIGH)
                    .addHeaders("Authorization","Bearer "+ Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN,null))
                    .addApplicationJsonBody(object).build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess();

                        }

                        @Override
                        public void onError(ANError anError) {
                            callbackInterface.onError(anError.toString());


                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



    }

}
