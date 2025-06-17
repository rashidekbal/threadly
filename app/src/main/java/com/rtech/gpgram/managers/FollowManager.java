package com.rtech.gpgram.managers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackIterface;
import com.rtech.gpgram.models.suggestUsersDataStructure;
import com.rtech.gpgram.constants.ApiEndPoints;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class FollowManager {
    SharedPreferences loginInfo;
    String myid,token;
    String Followurl= ApiEndPoints.FOLLOW;
    String UnFollowurl=ApiEndPoints.UNFOLLOW;
    ArrayList<suggestUsersDataStructure> dataList = new ArrayList<>();
Context context;
    public FollowManager(Context context){
        this.context=context;
        AndroidNetworking.initialize(context);
        this.loginInfo=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,MODE_PRIVATE);
        this.myid=loginInfo.getString(SharedPreferencesKeys.USER_ID,"null");
        this.token=loginInfo.getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }

    public void  follow(String userid, NetworkCallbackIterface callbackIterface) {

        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);

            AndroidNetworking.post(Followurl).setPriority(Priority.HIGH).addApplicationJsonBody(packet).addHeaders("Authorization", "Bearer ".concat(token)).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    callbackIterface.onSucess();

                }


                @Override
                public void onError(ANError anError) {
                    int errorCode=anError.getErrorCode();
                    Toast.makeText(context, Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
                    callbackIterface.onError(anError.toString());

                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void unfollow(String userid,NetworkCallbackIterface callbackIterface) {
        JSONObject packet = new JSONObject();
        try {
            packet.put("followingid", userid);

            AndroidNetworking.post(UnFollowurl).setPriority(Priority.HIGH).addHeaders("Authorization", "Bearer ".concat(token)).addBodyParameter(packet).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    callbackIterface.onSucess();

                }


                @Override
                public void onError(ANError anError) {
                    int errorCode=anError.getErrorCode();
                    Toast.makeText(context, Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
                    callbackIterface.onError(anError.toString());

                }
            });



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    }
