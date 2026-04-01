package com.rtech.threadly.network_managers;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.utils.CentralApiErrorHandler;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

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
                            try {
                                JSONObject errorObject=new JSONObject(anError.getErrorBody());
                                CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,Url,new Date().toString());
                                callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,Url,new Date().toString());
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,Url,new Date().toString() );
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,Url,new Date().toString());
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,Url,new Date().toString() );
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject, Url,new Date().toString() );
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,Url,new Date().toString());
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
    public static void patch(String Url,String token,JSONArray array, NetworkCallbackInterfaceJsonObject callbackInterfaceJsonObject){

        AndroidNetworking.patch(Url).setPriority(Priority.HIGH).addHeaders("Authorization","Bearer "+ token)
                .addJSONArrayBody(array)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callbackInterfaceJsonObject.onSuccess(jsonObject);

                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject, Url,new Date().toString() );
                            callbackInterfaceJsonObject.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject, url,new Date().toString() );
                            callBack.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

    }

    public static void delete(String url,String token,NetworkCallbackInterfaceJsonObject callBack){
        AndroidNetworking.delete(url).addHeaders("Authorization","Bearer "+token)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callBack.onSuccess(jsonObject);
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject, url,new Date().toString() );
                            callBack.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

    }
    public static void upload(String url, String token , File filepath,String key , String Tag, NetworkCallbackInterfaceWithProgressTracking callbackInterfaceWithProgressTracking){
        AndroidNetworking.upload(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+ token)
                .setTag(Tag)
                .addMultipartFile(key,filepath).build()
                .setUploadProgressListener(callbackInterfaceWithProgressTracking::progress).getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callbackInterfaceWithProgressTracking.onSuccess(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject, url,new Date().toString() );
                            callbackInterfaceWithProgressTracking.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
    }
    public static void upload(String url, String token , File filepath,String key ,String caption, String Tag, NetworkCallbackInterfaceWithProgressTracking callbackInterfaceWithProgressTracking){
        AndroidNetworking.upload(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+ token)
                .setTag(Tag)
                .addMultipartFile(key,filepath)
                .addMultipartParameter("caption",caption)
                .build()
                .setUploadProgressListener(callbackInterfaceWithProgressTracking::progress).getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterfaceWithProgressTracking.onSuccess(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject,url,new Date().toString() );
                            callbackInterfaceWithProgressTracking.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
    }
    public static void uploadWithType(String url, String token , File filepath,String key ,String type, String Tag, NetworkCallbackInterfaceWithProgressTracking callbackInterfaceWithProgressTracking){
        AndroidNetworking.upload(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+ token)
                .setTag(Tag)
                .addMultipartFile(key,filepath)
                .addMultipartParameter("type",type)
                .build()
                .setUploadProgressListener(callbackInterfaceWithProgressTracking::progress).getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterfaceWithProgressTracking.onSuccess(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            JSONObject errorObject=new JSONObject(anError.getErrorBody());
                            CentralApiErrorHandler.handleErrorCode(anError.getErrorCode(),errorObject, url,new Date().toString());
                            callbackInterfaceWithProgressTracking.onError(anError.getErrorCode(), errorObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }


}
