package com.rtech.threadly.network_managers;
import android.content.SharedPreferences;
import android.util.Log;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.LogTags;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.PreferenceUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthManager {
    SharedPreferences loginInfo;
    public AuthManager(){
        // Initialize any necessary components or configurations for authentication
        loginInfo = Core.getPreference();
    }
    public void LoginMobile(String mobile,String password,NetworkCallbackInterfaceJsonObject callback){
        String url=ApiEndPoints.LOGIN_MOBILE;
        JSONObject packet=new JSONObject();
        try {
            packet.put("userid",mobile);
            packet.put("password",password);
            NetworkingProvider.post(url ,packet, new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(int errorCode, JSONObject errorObject) {
                    callback.onError(errorCode,errorObject);
                    LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"api error login mobile with code : "+(errorCode));
                }
            });


        } catch (JSONException e) {

            throw new RuntimeException(e);
        }

    }
    public void ForgetPasswordWithMobile(String password, String token, NetworkCallbackInterface callback) {

        String url= ApiEndPoints.FORGET_PASSWORD_MOBILE;
        JSONObject data = new JSONObject();
        try {
            data.put("password", password);
            NetworkingProvider.patch(url,token,data,new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess();
                }

                @Override
                public void onError(int errorCode, JSONObject errorObject) {
                    callback.onError(Integer.toString(errorCode));
                    LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"api error ForgetPasswordWithMobile  with code : "+(errorCode));
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }


    }
    public void ForgetPasswordWithEmail(String password, String token, NetworkCallbackInterface callback) {
        String url= ApiEndPoints.FORGET_PASSWORD_EMAIL;
        JSONObject data = new JSONObject();
        try {
            data.put("password", password);
            NetworkingProvider.post(url, token, data, new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess();
                }

                @Override
                public void onError(int errorCode, JSONObject errorObject) {
                    callback.onError(Integer.toString(errorCode));
                    LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"api error ForgetPasswordWithEmail  with code : "+(errorCode));

                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }


    }
    public void LoginEmail(String email,String password,NetworkCallbackInterfaceJsonObject callback){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.LOGIN_EMAIL;
        JSONObject packet=new JSONObject();
        try {
            packet.put("userid",email);
            packet.put("password",password);
            NetworkingProvider.post(url, packet, new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(int errorCode, JSONObject errorObject) {
                    callback.onError(errorCode,errorObject);
                    LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"api error LoginEmail  with code : "+(errorCode));


                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public void LoginUserId(String email,String password,NetworkCallbackInterfaceJsonObject callback){
        Log.d("feddhit", "getLoggedInUserProfile: ");
        String url=ApiEndPoints.LOGIN_USERID;
        JSONObject packet=new JSONObject();
        try {
            packet.put("userid",email);
            packet.put("password",password);
            NetworkingProvider.post(url, packet, new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(int errorCode, JSONObject errorObject) {
                    callback.onError(errorCode, errorObject);
                    LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"api error LoginUserId  with code : "+(errorCode));

                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public  void logout(NetworkCallbackInterface callbackInterface){
        String url=ApiEndPoints.LOGOUT;
        NetworkingProvider.get(url, PreferenceUtil.getJWT(), new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                callbackInterface.onSuccess();
            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                callbackInterface.onError(Integer.toString(errorCode));
                LoggerUtil.log(LogTags.NETWORK_LOG.toString(),"api error logout  with code : "+(errorCode));


            }
        });
    }
    public void ResetPassword(String oldPassword, String newPassword, NetworkCallbackInterfaceJsonObject callback)throws JSONException{
        String Url=ApiEndPoints.RESET_PASSWORD_SETTING;
        JSONObject packet=new JSONObject();
            packet.put("oldPassword",oldPassword);
            packet.put("newPassword",newPassword);
            NetworkingProvider.post(Url,packet,callback);

    }

}
