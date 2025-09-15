package com.rtech.threadly.network_managers;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

public class MessageManager {
    static String TAG="MessageManager";
    public static void sendMessage(JSONObject object){
        String url= ApiEndPoints.SEND_MESSAGE;
        AndroidNetworking.post(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+ Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .addApplicationJsonBody(object)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject object1=response.optJSONObject("data");
                        String MsgUid=object1.optString("MsgUid");
                        int deliveryStatus=object1.optInt("deliveryStatus");
                        ReUsableFunctions.updateMessageStatus(MsgUid,deliveryStatus);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(Constants.NETWORK_ERROR_TAG.toString(), "Error in sending message via http request "+anError.getErrorDetail());

                    }
                });

    }
    public static void getaAndUpdatePendingMessagesFromServer(String senderUUid ){
        String url=ApiEndPoints.GET_PENDING_MESSAGES;
        JSONObject object=new JSONObject();
        try {
            object.put("senderUuid",senderUUid);
            AndroidNetworking.post(url).setPriority(Priority.HIGH)
                    .addHeaders("Authorization","Bearer "+Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                    .addApplicationJsonBody(object)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray data=response.optJSONArray("data");
                            if(data.length()>0){
                                for(int i=0;i<data.length();i++){
                                    JSONObject object1=data.optJSONObject(i);

                                    String MsgUid=object1.optString("messageUid");
                                    String replyToMsgUid=object1.optString("replyToMessageId");
                                    String senderUuid=object1.optString("senderUUId");
                                    String receiverUuid=object1.optString("recieverUUId");
                                    String type=object1.optString("type");
                                    String message=object1.optString("message");
                                    String timeStamp=ReUsableFunctions.toIso8601Utc(object1.optString("creationTime"));
                                    int deliveryStatus=object1.optInt("deliveryStatus");
                                    boolean isDeleted=object1.optInt("isDeleted")==1;
                                    String conversationUid=senderUUid+Core.getPreference().getString(SharedPreferencesKeys.UUID,"null");
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataBase.getInstance().dao().insertMessage(new MessageSchema(
                                                    MsgUid,conversationUid,replyToMsgUid,
                                                    senderUuid,receiverUuid,message,type,timeStamp,-1,isDeleted
                                            ));
                                        }
                                    });


                                }
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(Constants.NETWORK_ERROR_TAG.toString(), "onError from getAndUpdateRoute  "+anError.getErrorDetail());

                        }
                    });




        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public static void checkAndGetPendingMessages(){
        String url=ApiEndPoints.CHECK_PENDING_MESSAGES;
        AndroidNetworking.get(url).setPriority(Priority.HIGH)
                .addHeaders("Authorization","Bearer "+Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray data=response.optJSONArray("data");
                        if(data.length()>0 ){
                            for(int i=0;i<data.length();i++){
                                JSONObject object=data.optJSONObject(0);
                                String senderUUid=object.optString("senderUUid");
                                String senderUserId=object.optString("userid");
                                String senderUserName=object.optString("username");
                                String profile=object.optString("profilepic");
                                int PendingMessages=object.optInt("messagesPending");
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Core.AddNewConversationHistory(senderUUid);
                                        getaAndUpdatePendingMessagesFromServer(senderUUid);
                                    }
                                });


                            }

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(Constants.NETWORK_ERROR_TAG.toString(), "onError from check penidng route  "+anError.getErrorDetail());

                    }
                });
    }
    public static void setSeenMessage(String senderUUid,String receiverUUid)throws JSONException{
     String Url=ApiEndPoints.UPDATE_MSG_SEEN_STATUS;
     JSONObject object=new JSONObject();
     object.put("senderUUid",senderUUid);
     object.put("receiverUUid",receiverUUid);
     AndroidNetworking.post(Url).setPriority(Priority.HIGH)
             .addHeaders("Authorization","Bearer "+Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN,"null"))
             .addApplicationJsonBody(object)
             .build()
             .getAsJSONObject(new JSONObjectRequestListener() {
                 @Override
                 public void onResponse(JSONObject response) {
                     Log.d(TAG, "done");
                 }

                 @Override
                 public void onError(ANError anError) {
                     Log.d(TAG, "onError: "+anError.getErrorDetail());
                 }
             });

    }
}
