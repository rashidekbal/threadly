package com.rtech.threadly.services;

import android.app.Notification;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class FcmService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("tokenId", "onNewToken: "+token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
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
            ReUsableFunctions.addMessageToDb(object);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        generateNotification(message.getData().get("username"),message.getData().get("profile"),message.getData().get("msg"));
    }



    private void generateNotification(String username,String profile,String msg) {
        try {
            Bitmap icon=Glide.with(Threadly.getGlobalContext()).asBitmap().load(profile).submit().get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Notification notification=new Notification.Builder(Threadly.getGlobalContext()).setChannelId(Constants.MESSAGE_RECEIVED_CHANNEL.toString())
                .setContentTitle(username).setContentText(msg).setSmallIcon(R.drawable.splash).build();
        Core.getNotificationManager().notify(101,notification);

    }
}
