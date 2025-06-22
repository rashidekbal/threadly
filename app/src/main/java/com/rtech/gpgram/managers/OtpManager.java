package com.rtech.gpgram.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.constants.ApiEndPoints;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpManager {
    Context context;
    SharedPreferences loginInfo;
    public OtpManager(Context c) {
        // Initialize any necessary components or configurations for OTP management
        this.context = c;
        loginInfo = context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }
    public void SendOtpMobile(String mobile, NetworkCallbackInterface callbackInterface){
        // Implement the logic to send OTP to the mobile number
        // This could involve making a network request to your server with the provided mobile number
        // Once the response is received, you can call the callback methods to deliver the result

        // Example:
        // AndroidNetworking.post("YOUR_SEND_OTP_URL")
        //         .addBodyParameter("mobile", mobile)
        //         .build()
        //         .getAsJSONObject(new JSONObjectRequestListener() {
        //             @Override
        //             public void onResponse(JSONObject response) {
        //                 callbackInterface.onSuccess();
        //             }
        //
        //             @Override
        //             public void onError(ANError error) {
        //                 callbackInterface.onError(error.getMessage());
        //             }
        //         });
    }
    public void ResendOtpMobile(String mobile, NetworkCallbackInterface callbackInterface){
        // Implement the logic to resend OTP to the mobile number
        // This could involve making a network request to your server with the provided mobile number
        // Once the response is received, you can call the callback methods to deliver the result

        // Example:
        // AndroidNetworking.post("YOUR_RESEND_OTP_URL")
        //         .addBodyParameter("mobile", mobile)
        //         .build()
        //         .getAsJSONObject(new JSONObjectRequestListener() {
        //             @Override
        //             public void onResponse(JSONObject response) {
        //                 callbackInterface.onSuccess();
        //             }
        //
        //             @Override
        //             public void onError(ANError error) {
        //                 callbackInterface.onError(error.getMessage());
        //             }
        //         });
    }
    public void ForgetPasswordOptSendMobile(String mobile, NetworkCallbackInterface callbackInterface){
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
                            callbackInterface.onError(anError.getMessage());

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
    public void VerifyOtpMobile(String mobile, String otp, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface) {
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
                            callbackInterface.onError(anError.getMessage());
                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }



//    email sections starts here

    public void SendOtpEmail(String email, NetworkCallbackInterface callbackInterface){
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
                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void VerifyOtpEmail(String email, String otp, NetworkCallbackInterfaceWithJsonObjectDelivery callbackInterface) {
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
                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void ForgetPasswordOptSendEmail(String email, NetworkCallbackInterface callbackInterface){
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
                            callbackInterface.onError(Integer.toString(anError.getErrorCode()));

                        }
                    });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

}
