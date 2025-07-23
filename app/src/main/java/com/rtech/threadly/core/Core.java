package com.rtech.threadly.core;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.WorkManager;

import com.androidnetworking.AndroidNetworking;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.utils.ExoplayerUtil;

import java.util.concurrent.TimeUnit;

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
   }

   public static SharedPreferences getPreference(){
       return preferences;
   }
   public static WorkManager getWorkManager(){
       return workManager;
   }
   public static OkHttpClient getOkHttp(){
       return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).writeTimeout(120,TimeUnit.SECONDS).build();

   }
   public static NotificationManager getNotificationManager(){
       return notificationManager;
   }

}
