package com.rtech.threadly.services;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.MessagePageActivity;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.FcmManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import org.json.JSONException;
import org.json.JSONObject;
public class FcmService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FcmManager.UpdateFcmToken(token, new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {

                Core.getPreference().edit().putBoolean(SharedPreferencesKeys.IS_FCM_TOKEN_UPLOADED,true).apply();

            }

            @Override
            public void onError(String err) {


            }
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String broadcastType=message.getData().get("responseType");
        Log.d("recievedBrodcast", "onMessageReceived: "+broadcastType);
        switch (broadcastType){
            case "statusUpdate":
                StatusUpdateHandler(message);

                break;
            case  "chat":
                ChatReceivedHandler(message);
                break;
            case "postLike":
                PostLikedNotificationHandler(message);
                break;

        }
       }
    private void ChatReceivedHandler(RemoteMessage message){
        JSONObject object=new JSONObject();
        try {
            object.put("senderUuid",message.getData().get("senderUuid"));
            object.put("username",message.getData().get("username"));
            object.put("userid",message.getData().get("userid"));
            object.put("profile",message.getData().get("profile"));
            object.put("message",message.getData().get("msg"));
            object.put("MessageUid",message.getData().get("MsgUid"));
            object.put("ReplyTOMessageUid",message.getData().get("ReplyTOMsgUid"));
            object.put("type",message.getData().get("type"));
            object.put("timestamp",message.getData().get("timestamp"));
            object.put("deliveryStatus",Integer.parseInt(message.getData().get("deliveryStatus")));
            object.put("isDeleted",Boolean.parseBoolean(message.getData().get("isDeleted")));
            ReUsableFunctions.addMessageToDb(object,"r");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        generateMessageNotification(message.getData().get("username"),message.getData().get("userid"),message.getData().get("profile"),message.getData().get("senderUuid"),message.getData().get("msg"));


    }
    private void StatusUpdateHandler(RemoteMessage message){
        String MsgUid=message.getData().get("MsgUid");
        int deliveryStatus=Integer.parseInt(message.getData().get("deliveryStatus"));
        boolean isDeleted=Boolean.parseBoolean(message.getData().get("isDeleted"));
        ReUsableFunctions.updateMessageStatus(MsgUid,deliveryStatus);

    }
    private void PostLikedNotificationHandler(RemoteMessage message){
        String userId=message.getData().get("userId");
        String username=message.getData().get("username");
        String userProfile=message.getData().get("userprofile");
        int postId=Integer.parseInt(message.getData().get("postId"));
        String postLink=message.getData().get("postLink");
        int insertId=Integer.parseInt(message.getData().get("insertId"));
        ReUsableFunctions.addNotification(new NotificationSchema(Constants.POST_LIKE_NOTIFICATION.toString(),insertId,userId,userProfile,username,postId,postLink,false,false));
     Notification.Builder notification=new Notification.Builder(Threadly.getGlobalContext())
             .setSmallIcon(R.drawable.splash)
             .setChannelId(Constants.MESSAGE_RECEIVED_CHANNEL.toString())
             .setContentTitle(message.getData().get("userId")+ " liked your post");
     Core.getNotificationManager().notify(100,notification.build());
    }



    private void generateMessageNotification(String username, String userid, String profile, String uuid, String msg) {

        Notification notification=new Notification.Builder(Threadly.getGlobalContext())
                .setChannelId(Constants.MESSAGE_RECEIVED_CHANNEL.toString())
                .setContentTitle(username)
                .setContentText(msg).setSmallIcon(R.drawable.splash)
                .setContentIntent(getIntentMessagePage(uuid,userid,username,profile)).
                setAutoCancel(true).build();
        Core.getNotificationManager().notify(101,notification);

    }
    private PendingIntent getIntentMessagePage(String uuid,String userid,String username,String profile){
        Bundle data=new Bundle();
        data.putString("userid",userid);
        data.putString("username",username);
        data.putString("profilePic",profile);
        data.putString("uuid",uuid);
        data.putString("src","notification");
        Intent openMessageIntent=new Intent(Threadly.getGlobalContext(), MessagePageActivity.class);
        openMessageIntent.putExtras(data);
        return PendingIntent.getActivity(Threadly.getGlobalContext(),1001,openMessageIntent, PendingIntent.FLAG_MUTABLE);
    }
}
