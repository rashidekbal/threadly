package com.rtech.threadly.network_managers;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkingProvider {
    public static void get(String Url,String token ,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

            AndroidNetworking.get(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+token)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            callbackInterfaceJsonObject.onSuccess(jsonObject);

                        }

                        @Override
                        public void onError(ANError anError) {
                            callbackInterfaceJsonObject.onError(anError.getErrorCode());

                        }
                    });
        }
    public static void post(String Url,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ PreferenceUtil.getJWT())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }
    public static void post(String Url,JSONObject object,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH)
                .addApplicationJsonBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }

    public static void post(String Url, String token ,JSONObject object,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ token)
                .addApplicationJsonBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }

    public static void post(String Url, String token,JSONArray array, NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ token)
                .addJSONArrayBody(array)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }
    public static void patch(String Url,String token,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.patch(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }

    public static void patch(String Url,String token, JSONObject object,NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.patch(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ token)
                .addApplicationJsonBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }
    public static void patch(String Url,String token,JSONArray array, NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.post(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ token)
                .addJSONArrayBody(array)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        callbackInterfaceJsonObject.onError(anError.getErrorCode());

                    }
                });
    }
    public static void delete(String url,String token,JSONObject object,NetworkCallbackInterfaceJsonObject callBack){
        AndroidNetworking.delete(url).addHeaders("Authorization","Bearer "+token)
                .addApplicationJsonBody(object)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callBack.onSuccess(jsonObject);
                    }

                    @Override
                    public void onError(ANError anError) {
                        callBack.onError(anError.getErrorCode());
                    }
                });

    }
    public static void delete(String url,String token,JSONArray array,NetworkCallbackInterfaceJsonObject callBack){
        AndroidNetworking.delete(url).addHeaders("Authorization","Bearer "+token)
                .addJSONArrayBody(array)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callBack.onSuccess(jsonObject);
                    }

                    @Override
                    public void onError(ANError anError) {
                        callBack.onError(anError.getErrorCode());
                    }
                });

    }

}
