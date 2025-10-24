package com.rtech.threadly.network_managers;


import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.ApiEndPoints;

import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpManager {

    public OtpManager() {
        // Initialize any necessary components or configurations for OTP management

    }

    public void ForgetPasswordOptSendMobile(String mobile, NetworkCallbackInterface callbackInterface){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.FORGET_PASSWORD_MOBILE_OTP;
        JSONObject data = new JSONObject();
        try {
            data.put("phone", mobile);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(data)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess();
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callbackInterface.onError(anError.getMessage());

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
    public void VerifyOtpMobile(String mobile, String otp, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url = ApiEndPoints.VERIFY_MOBILE_OTP;
        JSONObject data = new JSONObject();
        try {
            data.put("phone", mobile);
            data.put("otp", otp);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(data)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callbackInterface.onError(anError.getMessage());
                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }



//    email sections starts here

    public void SendOtpEmail(String email, NetworkCallbackInterface callbackInterface){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.SEND_EMAIL_OTP;
        JSONObject packet=new JSONObject();
        try {
            packet.put("email",email);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess();
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void VerifyOtpEmail(String email, String otp, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface) {
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url = ApiEndPoints.VERIFY_EMAIL_OTP;
        JSONObject packet = new JSONObject();
        try {
            packet.put("email", email);
            packet.put("otp",otp);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(packet)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void ForgetPasswordOptSendEmail(String email, NetworkCallbackInterface callbackInterface){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.FORGET_PASSWORD_EMAIL_OTP;
        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addApplicationJsonBody(data)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callbackInterface.onSuccess();
                        }

                        @Override
                        public void onError(ANError anError) {
                            if(BuildConfig.DEBUG){
                                Log.d("ApiError", "Error"+ anError.getMessage());

                            }
                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

}
