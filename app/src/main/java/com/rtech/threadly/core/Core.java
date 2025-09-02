package com.rtech.threadly.core;

import static android.content.Context.MODE_PRIVATE;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.WorkManager;
import com.androidnetworking.AndroidNetworking;
import com.google.gson.JsonArray;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.SocketIo.SocketManager;

import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.ProfileManager;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;


public class Core {
    private static SharedPreferences preferences;
    private static WorkManager workManager;
    private static NotificationManager notificationManager;



   public static void init(Context context){
       ExoplayerUtil.init(context);
       AndroidNetworking.initialize(context);
       preferences=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,MODE_PRIVATE);
       workManager=WorkManager.getInstance(context);
       notificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
       notificationManager.createNotificationChannel(new NotificationChannel(Constants.MEDIA_UPLOAD_CHANNEL.toString(),"media Upload Notification", NotificationManager.IMPORTANCE_DEFAULT));
       notificationManager.createNotificationChannel(new NotificationChannel(Constants.MESSAGE_RECEIVED_CHANNEL.toString(),"for receiving messages",NotificationManager.IMPORTANCE_HIGH));
       String uuid=getPreference().getString(SharedPreferencesKeys.UUID,null) ;
       if(uuid!=null){
           startSocketEvents();
       }

   }

   public static SharedPreferences getPreference() {return preferences;
   }
   public static WorkManager getWorkManager(){
       return workManager;
   }
   public static OkHttpClient getOkHttp(){
       return new OkHttpClient().newBuilder().connectTimeout(120, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).writeTimeout(300,TimeUnit.SECONDS).build();

   }
   public static NotificationManager getNotificationManager(){
       return notificationManager;
   }

   public static Emitter.Listener StoC_Listener=new Emitter.Listener() {
       @Override
       public void call(Object... args) {
           JSONObject object =(JSONObject) args[0];

           String ConversationId=object.optString("senderUuid")+getPreference().getString(SharedPreferencesKeys.UUID, "null");
           String senderUuid=object.optString("senderUuid");
           String username=object.optString("username");
           String userid=object.optString("userid");
           String profile=object.optString("profile");
           String latestMsg=object.optString("msg");
           String MessageUid=object.optString("MsgUid");
           String ReplyTOMessageUid=object.optString("ReplyTOMsgUid");
           String type=object.optString("type");
           String timestamp=object.optString("timestamp");
           Executors.newSingleThreadExecutor().execute(() -> {
               HistorySchema History= DataBase.getInstance().historyOperator().getHistory(ConversationId);
               if(History==null){
                   DataBase.getInstance().historyOperator().insertHistory(new HistorySchema(ConversationId,username,userid,profile,senderUuid,latestMsg));
                   Log.d("Stoc", "call:  added new Conversation");
               }
               Log.d("StoC", "call: "+timestamp);
               DataBase.getInstance().dao().insertMessage(new MessageSchema(
                       MessageUid,
                       ConversationId,
                       ReplyTOMessageUid,
                       senderUuid,
                       getPreference().getString(SharedPreferencesKeys.UUID,null),
                       latestMsg,
                       type,
                       timestamp,
                       -1,
                       false
               ));
           });
       }
   };
   public static Emitter.Listener msgUuidGenerated=new Emitter.Listener() {

       @Override
       public void call(Object... args) {
           JSONObject object =(JSONObject) args[0];
           String ConversationId=object.optString("receiverUuid")+getPreference().getString(SharedPreferencesKeys.UUID, "null");
           String senderUuid=object.optString("senderUuid");
           String latestMsg=object.optString("msg");
           String MessageUid=object.optString("MsgUid");
           String ReplyTOMessageUid=object.optString("ReplyTOMsgUid");
           String type=object.optString("type");
           String receiverUid=object.optString("receiverUuid");
           String timestamp=object.optString("timestamp");
           int deliveryStatus=object.optInt("deliveryStatus");

           Executors.newSingleThreadExecutor().execute(() -> {
               DataBase.getInstance().dao().insertMessage(new MessageSchema(
                       MessageUid,
                       ConversationId,
                       ReplyTOMessageUid,
                       senderUuid,
                       receiverUid,
                       latestMsg,
                       type,
                       timestamp,
                       deliveryStatus,
                       false
               ));
               AddNewConversationHistory(receiverUid);
           });


       }
   };


   public static  void startSocketEvents(){
       //for socket connection;
       SocketManager.getInstance().connect();
       //for sending the uuid on connection
       SocketManager.getInstance().getSocket().emit("onConnect",getPreference().getString(SharedPreferencesKeys.UUID,"null"));
       SocketManager.getInstance().getSocket().on("StoC",StoC_Listener);
       SocketManager.getInstance().getSocket().on("msgUuidGenerated",msgUuidGenerated);

   }
   public static void sendCtoS(String uuid,String msg)throws JSONException {
       JSONObject object=new JSONObject();
       object.put("senderUuid",Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"));
       object.put("receiverUuid",uuid);
       object.put("senderName",Core.getPreference().getString(SharedPreferencesKeys.USER_NAME,"null"));
       object.put("senderUserId",Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"));
       object.put("senderProfilePic",Core.getPreference().getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null"));
       object.put("msg",msg);
       SocketManager.getInstance().getSocket().emit("CToS",object);
   }
   public static void AddNewConversationHistory(String ReceiverUuid) {

       String ConversationId = ReceiverUuid + getPreference().getString(SharedPreferencesKeys.UUID, "null");
       HistorySchema history = DataBase.getInstance().historyOperator().getHistory(ConversationId);
       if (history == null) {
           Log.d("notfound", "history not found ");
           new ProfileManager().GetProfileByUuid(ReceiverUuid, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
               @Override
               public void onSuccess(JSONObject response) {
                   JSONArray Array = response.optJSONArray("data");
                   assert Array != null;
                   if (Array.length() > 0) {
                       JSONObject object = Array.optJSONObject(0);
                       String username = object.optString("username");
                       String userid = object.optString("userid");
                       String profile = object.optString("profilepic");
                       Executors.newSingleThreadExecutor().execute(new Runnable() {
                           @Override
                           public void run() {
                               DataBase.getInstance().historyOperator().insertHistory(new HistorySchema(ReceiverUuid + getPreference().getString(SharedPreferencesKeys.UUID, "null")
                                       , username, userid, profile, ReceiverUuid, "null"));
                               Log.d("notfound", "data inserted ");
                           }
                       });


                   } else {
                       Log.d("notfound", "no such user inserted ");

                   }

               }

               @Override
               public void onError(String err) {
                   Log.d("errorFetching", err);

               }
           });
       } else {
           Log.d("notfound", "history  found ");
       }


   };

   }




