package com.rtech.threadly.core;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.WorkManager;
import com.androidnetworking.AndroidNetworking;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.SocketIo.SocketManager;

import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.network_managers.MessageManager;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.MessengerUtils;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONException;
import org.json.JSONObject;

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
       notificationManager=(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
       notificationManager.createNotificationChannel(new NotificationChannel(Constants.MEDIA_UPLOAD_CHANNEL.toString(),"media Upload Notification", NotificationManager.IMPORTANCE_DEFAULT));
       notificationManager.createNotificationChannel(new NotificationChannel(Constants.MESSAGE_RECEIVED_CHANNEL.toString(),"for receiving messages",NotificationManager.IMPORTANCE_HIGH));
       notificationManager.createNotificationChannel(new NotificationChannel(Constants.MISC_CHANNEL.toString(),"misc",NotificationManager.IMPORTANCE_DEFAULT));
       String uuid=getPreference().getString(SharedPreferencesKeys.UUID,null) ;
       if(uuid!=null){
           startSocketEvents();
           MessageManager.checkAndGetPendingMessages();
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
           String link=object.optString("link");
           int postId=object.optInt("postId");
           String timestamp=object.optString("timestamp");
           Executors.newSingleThreadExecutor().execute(() -> {
               HistorySchema History= DataBase.getInstance().historyOperator().getHistory(ConversationId);
               if(History==null){
                   DataBase.getInstance().historyOperator().insertHistory(new HistorySchema(ConversationId,username,userid,profile,senderUuid,latestMsg,ReUsableFunctions.getTimestamp()));
                   Log.d("Stoc", "call:  added new Conversation");
               }else{
                   DataBase.getInstance().historyOperator().updateTimeStamp(ConversationId,ReUsableFunctions.getTimestamp());
               }
               Log.d("StoC", "call: "+timestamp);
               DataBase.getInstance().MessageDao().insertMessage(new MessageSchema(
                       MessageUid,
                       ConversationId,
                       ReplyTOMessageUid,
                       senderUuid,
                       getPreference().getString(SharedPreferencesKeys.UUID,null),
                       latestMsg,
                       type,
                       postId,
                       link,
                       timestamp,
                       -1,
                       false
               ));
           });
       }
   };
   public static Emitter.Listener MsgStatusUpdate=new Emitter.Listener() {

       @Override
       public void call(Object... args) {
           JSONObject object =(JSONObject) args[0];
           String MessageUid=object.optString("MsgUid");
           int deliveryStatus=object.optInt("deliveryStatus");
           ReUsableFunctions.updateMessageStatus(MessageUid,deliveryStatus);


       }
   };
   public static Emitter.Listener msg_status_changed_event=new Emitter.Listener() {
       @Override
       public void call(Object... args) {
           JSONObject object=(JSONObject) args[0];
           String MsgUid=object.optString("MsgUid");
           int deliveryStatus=object.optInt("deliveryStatus");
           ReUsableFunctions.updateMessageStatus(MsgUid,deliveryStatus);

       }
   };
public static Emitter.Listener msg_UnSend_eventHandler=new Emitter.Listener() {
    @Override
    public void call(Object... args) {
        JSONObject object=(JSONObject) args[0];
        String MsgUid=object.optString("MsgUid");
        ReUsableFunctions.DeleteMessage(MsgUid);
    }
};

   public static  void startSocketEvents(){
       //for socket connection;
       SocketManager.getInstance().connect();
       //for sending the uuid on connection
       SocketManager.getInstance().getSocket().emit("onConnect",getPreference().getString(SharedPreferencesKeys.UUID,"null"));
       SocketManager.getInstance().getSocket().on("StoC",StoC_Listener);
       SocketManager.getInstance().getSocket().on("MsgStatusUpdate",MsgStatusUpdate);
       SocketManager.getInstance().getSocket().on("msg_status_changed_event",msg_status_changed_event);
       SocketManager.getInstance().getSocket().on("msg_unSend_event",msg_UnSend_eventHandler);

   }
   public static void sendCtoS(String uuid,String msg,String type,String link,int postId,@Nullable String notificationText)throws JSONException {
       String timestamp=ReUsableFunctions.getTimestamp();
       String MsgUid=ReUsableFunctions.GenerateUUid();
       String senderUuid=Core.getPreference().getString(SharedPreferencesKeys.UUID,"null");
       String senderName=Core.getPreference().getString(SharedPreferencesKeys.USER_NAME,"null");
       String senderUserId=Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null");
       String senderProfilePic=Core.getPreference().getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null");
       JSONObject object=new JSONObject();
       object.put("timestamp",timestamp);
       object.put("MsgUid",MsgUid);
       object.put("senderUuid",senderUuid);
       object.put("receiverUuid",uuid);
       object.put("senderName",senderName);
       object.put("senderUserId",senderUserId);
       object.put("senderProfilePic",senderProfilePic);
       object.put("msg",msg);
       object.put("type",type);
       object.put("link",link);
       object.put("postId",postId);
       object.put("notificationText",notificationText);

       if (!SocketManager.getInstance().getSocket().connected()){
           Log.d(Constants.NETWORK_ERROR_TAG.toString(), "sendCtoS: socket not connected adding fall back");
           MessageManager.sendMessage(object);

       }else{
           SocketManager.getInstance().getSocket().emit("CToS",object);
       }
       MessengerUtils.AddNewConversationHistory(uuid);
       Executors.newSingleThreadExecutor().execute(new Runnable() {
           @Override
           public void run() {

               DataBase.getInstance().MessageDao().insertMessage(new MessageSchema(
                       MsgUid,
                       uuid+senderUuid,
                       "null",
                       senderUuid,
                       uuid,
                       msg,
                       type,
                       postId,
                       link,
                       timestamp,
                       0,
                       false
               ));
           }
       });

   }

    public static void sendCtoS(MessageSchema messageSchema)throws JSONException {
        String timestamp=messageSchema.getTimestamp();
        String MsgUid=messageSchema.getMessageUid();
        String senderUuid=messageSchema.getSenderId();
        String senderName=Core.getPreference().getString(SharedPreferencesKeys.USER_NAME,"null");
        String senderUserId=Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null");
        String senderProfilePic=Core.getPreference().getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null");
        JSONObject object=new JSONObject();
        object.put("timestamp",timestamp);
        object.put("MsgUid",MsgUid);
        object.put("senderUuid",senderUuid);
        object.put("receiverUuid",messageSchema.getReceiverId());
        object.put("senderName",senderName);
        object.put("senderUserId",senderUserId);
        object.put("senderProfilePic",senderProfilePic);
        object.put("msg",messageSchema.getMsg());
        object.put("type",messageSchema.getType());
        object.put("link",messageSchema.getPostLink());
        object.put("postId",messageSchema.getPostId());

        if (!SocketManager.getInstance().getSocket().connected()){
            Log.d(Constants.NETWORK_ERROR_TAG.toString(), "sendCtoS: socket not connected adding fall back");
            MessageManager.sendMessage(object);

        }else{
            SocketManager.getInstance().getSocket().emit("CToS",object);
        }
    }
}





