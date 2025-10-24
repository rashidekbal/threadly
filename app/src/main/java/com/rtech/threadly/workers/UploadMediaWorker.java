package com.rtech.threadly.workers;

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
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.network_managers.PostsManager;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class UploadMediaWorker extends Worker {
    String TAG ="uploadError";
    PostsManager postsManager=new PostsManager();
    File media;
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
        NetworkCallbackInterfaceWithProgressTracking callbackInterfaceWithProgressTracking=new NetworkCallbackInterfaceWithProgressTracking() {
            @Override
            public void onSuccess(JSONObject response) {
                showUploadProgressNotification(0,0,false,true);
                media.delete();
                isSucess[0]=true;
                latch.countDown();
            }

            @Override
            public void onError(String err) {
                showUploadProgressNotification(0,0,false,false);
                Log.d(TAG, "onError: "+err);

                media.delete();
                isSucess[0]=false;
                latch.countDown();

            }

            @Override
            public void progress(long bytesUploaded, long totalBytes) {

                showUploadProgressNotification((int)totalBytes,(int)bytesUploaded,true,isSucess[0]);

            }
        };


        if(type.equals("image")){
            postsManager.uploadImagePost(media,caption,callbackInterfaceWithProgressTracking);
        }
        else {
            postsManager.uploadVideoPost(media,caption,callbackInterfaceWithProgressTracking);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            return Result.failure();
        }

        return isSucess[0]?Result.success():Result.failure();
    }

    private void showUploadProgressNotification(int max,int current,boolean uploading,boolean isSuccess){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(Threadly.getGlobalContext()).setChannelId(Constants.MEDIA_UPLOAD_CHANNEL.toString()).setContentTitle("Uploading media").setSmallIcon(R.drawable.splash);
        if(uploading){

                builder.setOngoing(true).setProgress(max,current,false).setOnlyAlertOnce(true);}
        else{
            builder.setOngoing(false).setProgress(0,0,false).setOnlyAlertOnce(false);
            if(isSuccess){
                builder.setContentText("upload success");
            }else{
                builder.setContentText("upload failed");
            }
        }



        Core.getNotificationManager().notify(201,builder.build());
    }
}
