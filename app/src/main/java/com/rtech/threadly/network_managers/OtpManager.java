package com.rtech.threadly.network_managers;


import android.util.Log;

import com.rtech.threadly.constants.ApiEndPoints;

import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpManager {

    public OtpManager() {
        // Initialize any necessary components or configurations for OTP management

    }

    public void ForgetPasswordOptSendMobile(String mobile, NetworkCallbackInterfaceJsonObject callbackInterface){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url= ApiEndPoints.FORGET_PASSWORD_MOBILE_OTP;
        JSONObject data = new JSONObject();
        try {
            data.put("phone", mobile);
            NetworkingProvider.post(url, data, callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500);
        }


    }
    public void VerifyOtpMobile(String mobile, String otp, NetworkCallbackInterfaceJsonObject callbackInterface) {
        String url = ApiEndPoints.VERIFY_MOBILE_OTP;
        JSONObject data = new JSONObject();
        try {
            data.put("phone", mobile);
            data.put("otp", otp);
            NetworkingProvider.post(url, data, callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500);
        }

    }



//    email sections starts here

    public void SendOtpEmail(String email, NetworkCallbackInterfaceJsonObject callbackInterface){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.SEND_EMAIL_OTP;
        JSONObject packet=new JSONObject();
        try {
            packet.put("email",email);
            NetworkingProvider.post(url,packet,callbackInterface);

        } catch (JSONException e) {
          callbackInterface.onError(500);
        }

    }

    public void VerifyOtpEmail(String email, String otp, NetworkCallbackInterfaceJsonObject callbackInterface) {

        String url = ApiEndPoints.VERIFY_EMAIL_OTP;
        JSONObject packet = new JSONObject();
        try {
            packet.put("email", email);
            packet.put("otp",otp);
            NetworkingProvider.post(url,packet,callbackInterface);



        } catch (JSONException e) {
           callbackInterface.onError(500);
        }

    }

    public void ForgetPasswordOptSendEmail(String email, NetworkCallbackInterfaceJsonObject callbackInterface){
        String url= ApiEndPoints.FORGET_PASSWORD_EMAIL_OTP;
        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
            NetworkingProvider.post(url,data,callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500);
        }


    }

}
