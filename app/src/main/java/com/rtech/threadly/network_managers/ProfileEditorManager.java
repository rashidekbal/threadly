package com.rtech.threadly.network_managers;

import android.content.SharedPreferences;

import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ProfileEditorManager {

    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;

    public ProfileEditorManager() {

        this.loginInfo = Core.getPreference();
        this.editor= loginInfo.edit();


    }
    private String getToken(){
        return PreferenceUtil.getJWT();
    }
    public void UpdateName(String name, NetworkCallbackInterfaceJsonObject callbackInterface){
        String url= ApiEndPoints.EDIT_USERNAME;
        JSONObject packet=new JSONObject();

        try {
            packet.put("name",name);
            NetworkingProvider.patch(url,getToken(),packet,callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500, new JSONObject());
        }

    }
    public void UpdateUserid(String userid, NetworkCallbackInterfaceJsonObject callbackInterface){
        String url= ApiEndPoints.EDIT_USERID;
        JSONObject packet=new JSONObject();

        try {
            packet.put("newUserId",userid);
            NetworkingProvider.patch(url,getToken(),packet,callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500,new JSONObject() );
        }

    }


    public void UpdateUserBio(String BioText, NetworkCallbackInterfaceJsonObject callbackInterface){
        String url= ApiEndPoints.EDIT_BIO;
        JSONObject packet=new JSONObject();

        try {
            packet.put("bioText",BioText);
            NetworkingProvider.patch(url,getToken(),packet,callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500, new JSONObject());
        }

    }
    public final void ChangeUserProfile(File picture,NetworkCallbackInterfaceWithProgressTracking callback){
        String url=ApiEndPoints.EDIT_PROFILE_PICTURE;
        NetworkingProvider.upload(url,getToken(),picture,"image","uploadProfile",callback);

    }





    public final void updatePreferences(String token,String userid) {

        editor.putString(SharedPreferencesKeys.JWT_TOKEN, token);
        editor.putString(SharedPreferencesKeys.USER_ID, userid);
        editor.apply();
    }
    public final void updatePreferences(String username) {
        editor.putString(SharedPreferencesKeys.USER_NAME, username);
        editor.apply();
    }
    public final void updateUserProfile(String profilePic) {
        editor.putString(SharedPreferencesKeys.USER_PROFILE_PIC, profilePic);
        editor.apply();
    }
}
