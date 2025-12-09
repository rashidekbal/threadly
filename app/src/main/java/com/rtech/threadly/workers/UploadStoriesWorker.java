package com.rtech.threadly.workers;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.network_managers.StoriesManager;

import org.json.JSONObject;

import java.io.File;

public class UploadStoriesWorker extends Worker {
    StoriesManager storiesManager;
    public UploadStoriesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.storiesManager=new StoriesManager();
    }

    @NonNull
    @Override
    public Result doWork() {
        int notificationCode=(int)Math.round(Math.random()*9999);
        boolean[] isSuccess={false};
        Data data=getInputData();
        String type = data.getString("type");
        String path=data.getString("path");
        File media=new File(path!=null?path:"null");
        if(!media.exists()) return Result.failure();
        storiesManager.AddStory(media, type, new NetworkCallbackInterfaceWithProgressTracking() {
            @Override
            public void onSuccess(JSONObject response) {
                media.delete();
               showNotificationWithProgress(0,0,false,true,notificationCode);
                isSuccess[0]=true;

            }

            @Override
            public void onError(String err) {
                media.delete();
                showNotificationWithProgress(0,0,false,false,notificationCode);
                Log.d("storyUploadError", "onError: "+err);
                isSuccess[0]=false;

            }

            @Override
            public void progress(long bytesUploaded, long totalBytes) {
                showNotificationWithProgress((int)totalBytes,(int)bytesUploaded,true,false,notificationCode);

            }
        });
        return isSuccess[0]?Result.success():Result.failure();
    }
    private void showNotificationWithProgress(int max,int current,boolean isUploading,boolean isSuccess,int notificationCode){
        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext()).setChannelId(Constants.MEDIA_UPLOAD_CHANNEL.toString())
                .setSmallIcon(R.drawable.splash)
                .setContentTitle("Uploading Story");
        if(isUploading){
            notification.setOngoing(true).setProgress(max,current,false).setOnlyAlertOnce(true);
        }else{
            notification.setOngoing(false).setProgress(0,0,false).setOnlyAlertOnce(false);
            if(isSuccess){
                notification.setContentText("Upload Success");
            }else{
                notification.setContentText("Upload Failed");
            }
        }
        Core.getNotificationManager().notify(notificationCode,notification.build());
    }
//    private void ShowNotification(String title,String content,int notificationCode){
//        Notification notification=new Notification.Builder(Threadly.getGlobalContext())
//                .setSmallIcon(R.drawable.splash)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setOngoing(true)
//                .setChannelId(Constants.MEDIA_UPLOAD_CHANNEL.toString())
//                .build();
//        Core.getNotificationManager().notify(notificationCode,notification);
//    }

}
