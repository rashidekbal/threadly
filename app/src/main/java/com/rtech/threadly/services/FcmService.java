package com.rtech.threadly.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.Messanger.MessengerMainMessagePageActivity;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.FcmManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executors;

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
        Log.d("receivedBroadcast", "onMessageReceived: "+broadcastType);
        switch (Objects.requireNonNull(broadcastType)){
            case "statusUpdate":
                StatusUpdateHandler(message);
                break;
            case  "chat":
                ChatReceivedHandler(message);
                break;
            case "postLike":
                PostLikedNotificationHandler(message);
                break;
            case "postUnLike":
                postUnlikedNotificationHandler(message);
                break;
            case "newFollower":
                newFollowerController(message);
                break;
            case "UnFollow":
                unFollowNotifyController(message);
                break;
            case "commentLike":
                commentLikeNotifyController(message);
                break;
            case "commentUnlike":
                commentUnlikeHandler(message);
                break;
            case "logout":
                LogOutSignalHandler(message);
                break;

        }
       }

    private void commentUnlikeHandler(RemoteMessage message) {
        String userId=message.getData().get("userId");
        int commentId=Integer.parseInt(Objects.requireNonNull(message.getData().get("commentId")));
        Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().notificationDao().deleteCommentLikeNotification(userId,commentId));
    }

    private void commentLikeNotifyController(RemoteMessage message) {
        String userId=message.getData().get("userId");
        String username=message.getData().get("username");
        String profile=message.getData().get("profile");
        String postLink=message.getData().get("postLink");
        int postId=Integer.parseInt(Objects.requireNonNull(message.getData().get("postId")));
        int commentId=Integer.parseInt(Objects.requireNonNull(message.getData().get("commentId")));
        ReUsableFunctions.addNotification(new NotificationSchema(Constants.COMMENT_LIKE_NOTIFICATION.toString(),0,userId,profile,username,postId,commentId,postLink,false,false,ReUsableFunctions.getTimestamp()));
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
            object.put("deliveryStatus",Integer.parseInt(Objects.requireNonNull(message.getData().get("deliveryStatus"))));
            object.put("isDeleted",Boolean.parseBoolean(message.getData().get("isDeleted")));
            ReUsableFunctions.addMessageToDb(object,"r");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        generateMessageNotification(message.getData().get("username"),message.getData().get("userid"),message.getData().get("profile"),message.getData().get("senderUuid"),message.getData().get("msg"));


    }
    private void StatusUpdateHandler(RemoteMessage message){
        String MsgUid=message.getData().get("MsgUid");
        int deliveryStatus=Integer.parseInt(Objects.requireNonNull(message.getData().get("deliveryStatus")));
//        boolean isDeleted=Boolean.parseBoolean(message.getData().get("isDeleted"));
        ReUsableFunctions.updateMessageStatus(MsgUid,deliveryStatus);

    }
    private void PostLikedNotificationHandler(RemoteMessage message){
        String userId=message.getData().get("userId");
        String username=message.getData().get("username");
        String userProfile=message.getData().get("userprofile");
        int postId=Integer.parseInt(Objects.requireNonNull(message.getData().get("postId")));
        String postLink=message.getData().get("postLink");
        int insertId=Integer.parseInt(Objects.requireNonNull(message.getData().get("insertId")));
        ReUsableFunctions.addNotification(new NotificationSchema(Constants.POST_LIKE_NOTIFICATION.toString(),insertId,userId,userProfile,username,postId,0,postLink,false,false,ReUsableFunctions.getTimestamp()));
    }
    private void postUnlikedNotificationHandler(RemoteMessage message){
        String userId=message.getData().get("userId");
        int postId=Integer.parseInt(Objects.requireNonNull(message.getData().get("postId")));
        Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().notificationDao().deletePostLikeNotification(userId,postId));


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
        Intent openMessageIntent=new Intent(Threadly.getGlobalContext(), MessengerMainMessagePageActivity.class);
        openMessageIntent.putExtras(data);
        return PendingIntent.getActivity(Threadly.getGlobalContext(),1001,openMessageIntent, PendingIntent.FLAG_MUTABLE);
    }
    private void LogOutSignalHandler(RemoteMessage message){
        if(Core.getPreference().getBoolean(SharedPreferencesKeys.IS_LOGGED_IN,false)&&Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null").equals(message.getData().get("userId"))){
            ReUsableFunctions.logoutWithoutActivity();
            Notification.Builder notification=new Notification.Builder(Threadly.getGlobalContext())
                    .setContentTitle("new Device login detected")
                    .setContentText("you have been logged out... ").setChannelId(Constants.MISC_CHANNEL.toString())
                    .setSmallIcon(R.drawable.splash);
            Core.getNotificationManager().notify(1,notification.build());
        }

    }
    private void newFollowerController(RemoteMessage message){
        String userId=message.getData().get("userid");
        String username=message.getData().get("username");
        String profile=message.getData().get("profile");
        boolean isFollowed=Boolean.parseBoolean(message.getData().get("isFollowed"));
        ReUsableFunctions.addNotification(new NotificationSchema(Constants.FOLLOW_NOTIFICATION.toString(),0,userId,profile,username,0,0,"",isFollowed,false,ReUsableFunctions.getTimestamp()));

    }
    private void unFollowNotifyController(RemoteMessage message){
        String userId=message.getData().get("userId");
        Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().notificationDao().deleteFollowNotification(userId,Constants.FOLLOW_NOTIFICATION.toString()));

    }
}
