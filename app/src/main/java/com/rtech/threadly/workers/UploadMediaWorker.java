package com.rtech.threadly.workers;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.PostsManager;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class UploadMediaWorker extends Worker {
    String TAG ="uploadError";
    PostsManager postsManager=new PostsManager();
    File media;
    private int UPLOAD_COMPLETE_CODE=100;
    private int UPLOAD_FAILED_CODE=101;
    public UploadMediaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Data data = getInputData();
        //extract all data passed
        String type = data.getString("type");
        String path = data.getString("path");
        String caption = data.getString("caption");
        //create a file reference
        media = new File(path);
        //check files  existance
        if (!media.exists()) {
            return Result.failure();
        }
        boolean [] isSucess={false};
        CountDownLatch latch=new CountDownLatch(1);
        NetworkCallbackInterfaceWithJsonObjectDelivery callback=new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                showNotification("Upload Complete","Your post has been uploaded successfully",UPLOAD_COMPLETE_CODE);
                media.delete();
                isSucess[0]=true;
                latch.countDown();


            }

            @Override
            public void onError(String err) {
                showNotification("Upload Failed","Your post has not been uploaded",UPLOAD_FAILED_CODE);
                Log.d(TAG, "onError: "+err);
                media.delete();
                isSucess[0]=false;
                latch.countDown();

            }
        };

        if(type.equals("image")){
            postsManager.uploadImagePost(media,caption,callback);
        }
        else {
            postsManager.uploadVideoPost(media,caption,callback);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            return Result.failure();
        }

        return isSucess[0]?Result.success():Result.failure();
    }
    private void showNotification(String title,String msg,int notificationCode){
        Notification notification=new Notification.Builder(Threadly.getGlobalContext())
                .setSmallIcon(R.drawable.splash)
                .setContentTitle(title)
                .setContentText(msg)
                .setChannelId(Constants.MEDIA_UPLOAD_CHANNEL.toString())
                .build();
        Core.getNotificationManager().notify(notificationCode,notification);
    }
}
