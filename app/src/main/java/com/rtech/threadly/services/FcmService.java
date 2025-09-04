package com.rtech.threadly.services;
import android.app.Notification;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
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
        switch (broadcastType){
            case "statusUpdate":
                StatusUpdateHandler(message);

                break;
            case  "chat":
                ChatReceivedHandler(message);
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



        generateNotification(message.getData().get("username"),message.getData().get("profile"),message.getData().get("msg"));


    }
    private void StatusUpdateHandler(RemoteMessage message){
        String MsgUid=message.getData().get("MsgUid");
        int deliveryStatus=Integer.parseInt(message.getData().get("deliveryStatus"));
        boolean isDeleted=Boolean.parseBoolean(message.getData().get("isDeleted"));
        ReUsableFunctions.updateMessageStatus(MsgUid,deliveryStatus);

    }



    private void generateNotification(String username,String profile,String msg) {

        Notification notification=new Notification.Builder(Threadly.getGlobalContext()).setChannelId(Constants.MESSAGE_RECEIVED_CHANNEL.toString())
                .setContentTitle(username).setContentText(msg).setSmallIcon(R.drawable.splash).build();
        Core.getNotificationManager().notify(101,notification);

    }
}
