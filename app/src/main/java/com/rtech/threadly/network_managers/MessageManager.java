package com.rtech.threadly.network_managers;



import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.MessengerUtils;
import com.rtech.threadly.utils.PreferenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

public class MessageManager {
    static String TAG="MessageManager";
    public static void sendMessage(JSONObject object){
        String url= ApiEndPoints.SEND_MESSAGE;
        NetworkingProvider.post(url, PreferenceUtil.getJWT(), object, new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONObject object1=response.optJSONObject("data");
                String MsgUid=object1.optString("MsgUid");
                int deliveryStatus=object1.optInt("deliveryStatus");
                ReUsableFunctions.updateMessageStatus(MsgUid,deliveryStatus);
            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                Log.d(Constants.NETWORK_ERROR_TAG.toString(), "Error in sending message via http request with code  : "+errorCode);
            }
        });


    }
    public static void getaAndUpdatePendingMessagesFromServer(String senderUUid ){
        String url=ApiEndPoints.GET_PENDING_MESSAGES;
        JSONObject object=new JSONObject();
        try {
            object.put("senderUuid",senderUUid);
            NetworkingProvider.post(url, PreferenceUtil.getJWT(), object, new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {

                    JSONArray data=response.optJSONArray("data");
                    assert data != null;
                    if(data.length()>0){
                        try{
                            new MessengerUtils().OrganizeChats(data);
                        } catch (Exception e) {
                            Log.d(TAG, "exception: "+e.toString());
                        }
                    }
                }

                @Override
                public void onError(int errorCode, JSONObject errorObject) {
                    Log.d(Constants.NETWORK_ERROR_TAG.toString(), "onError from getAndUpdateRoute  with code : "+errorCode);
                }
            });

        } catch (JSONException e) {
            Log.d(Constants.NETWORK_ERROR_TAG.toString(), "onError from getAndUpdateRoute : "+e.toString());
        }

    }
    public static void checkAndGetPendingMessages(){

        String url=ApiEndPoints.CHECK_PENDING_MESSAGES;
        NetworkingProvider.get(url, PreferenceUtil.getJWT(), new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray data= null;
                try {
                    data = response.getJSONArray("data");

                    if(data.length()>0 ){
                        for(int i=0;i<data.length();i++){
                            JSONObject object=data.optJSONObject(i);
                            String senderUUid=object.optString("senderUUid");
                            String senderUserId=object.optString("userid");
                            String senderUserName=object.optString("username");
                            String profile=object.optString("profilepic");
                            int PendingMessages=object.optInt("messagesPending");
                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    getaAndUpdatePendingMessagesFromServer(senderUUid);
                                }
                            });


                        }

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }

            @Override
            public void onError(int errorCode, JSONObject errorObject) {
                LoggerUtil.log("checkPending","error: with code :  "+errorCode);
            }
        });

    }
    public static void setSeenMessage(List<String> MessageUids, String senderUUid, String receiverUUid, NetworkCallbackInterfaceJsonObject  callbackInterface)throws JSONException{
     String Url=ApiEndPoints.UPDATE_MSG_SEEN_STATUS;
     JSONObject object=new JSONObject();
     JSONArray Uids=new JSONArray();
     for(String uids:MessageUids){
         Uids.put(uids);
     }
     object.put("senderUUid",senderUUid);
     object.put("receiverUUid",receiverUUid);
     object.put("uids",Uids);
     NetworkingProvider.post(Url,PreferenceUtil.getJWT(),object,callbackInterface);


    }
    public static void UploadMsgMedia(File filepath,String Tag, NetworkCallbackInterfaceWithProgressTracking callbackInterfaceWithProgressTracking){
        String url=ApiEndPoints.UPLOAD_MEDIA_MESSAGE;
        NetworkingProvider.upload(url,PreferenceUtil.getJWT(),filepath,"media",Tag,callbackInterfaceWithProgressTracking);


    }

    public static void CancelMessageMediaUploadRequest(String Tag){
        AndroidNetworking.cancel(Tag);
    }

    public static void GetAllChatsAssociatedWithUser(NetworkCallbackInterfaceJsonObject callback){
        String Url=ApiEndPoints.GET_ALL_CHATS;
        NetworkingProvider.get(Url,PreferenceUtil.getJWT(),callback);


    }
    public static void DeleteMessageForLoggedInUser(String MsgUid, String Role, NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url=ApiEndPoints.DELETE_MSG_WITH_ROLE;
        JSONObject packet=new JSONObject();
        try {
            packet.put("MsgUid",MsgUid);
            packet.put("Role",Role);
            NetworkingProvider.patch(Url,PreferenceUtil.getJWT(),packet,callbackInterface);

        } catch (JSONException e) {
            callbackInterface.onError(500, new JSONObject());
        }

    }
    public static  void unSendMessage(String messageUid,String receiverUUid,NetworkCallbackInterfaceJsonObject callbackInterface){
        String Url=ApiEndPoints.UN_SEND_MESSAGE;
        JSONObject packet=new JSONObject();
        try {
            packet.put("MsgUid",messageUid);
            packet.put("receiverUUid",receiverUUid);
            NetworkingProvider.patch(Url,PreferenceUtil.getJWT(),packet,callbackInterface);

        } catch (JSONException e) {
           callbackInterface.onError(500, new JSONObject());
        }


    }
}
