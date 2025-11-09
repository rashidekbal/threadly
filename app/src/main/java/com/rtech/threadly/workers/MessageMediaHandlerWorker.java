package com.rtech.threadly.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.constants.MessageStateEnum;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithProgressTracking;
import com.rtech.threadly.network_managers.MessageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageMediaHandlerWorker extends Worker {
    Context context;
    String TAG = "MEDIA_UPLOADING_TASK";
    public MessageMediaHandlerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean[] success={false};
        Data data=getInputData();
        String path=data.getString("path");
        String messageUid=data.getString("messageUid");
        if(path==null){return Result.failure();}
        if(messageUid==null) {return Result.failure();}
        File file =new File(path);
        if(!file.exists()){return Result.failure();}
        CountDownLatch latch=new CountDownLatch(1);

        MessageManager.UploadMsgMedia(file, messageUid, new NetworkCallbackInterfaceWithProgressTracking() {
            @Override
            public void onSuccess(JSONObject response) {
                success[0]=onSuccessCleanUp(messageUid,response);
                latch.countDown();




            }

            @Override
            public void onError(String err) {
                success[0]=false;
                onFailureCleanUp(latch,messageUid,err);

            }

            @Override
            public void progress(long bytesUploaded, long totalBytes) {
                updateProgress(messageUid,bytesUploaded,totalBytes);


            }
        });

        try {
            latch.await();
            if(success[0]){

                return Result.success();
            }else{

                return Result.failure();
            }

        } catch (InterruptedException e) {
            Log.d(TAG, "message media upload worker error  ");
            return Result.failure();
        }

    }

    private void onFailureCleanUp(CountDownLatch latch, String messageUid, String err) {
        Log.d(TAG, "onFailureCleanUp: "+err);
        DataBase.getInstance().MessageDao().updatePostLinkWithState( messageUid,null, MessageStateEnum.FAILED.toString());
        latch.countDown();
    }

    private boolean onSuccessCleanUp( String messageUid, JSONObject response) {
        JSONObject data=response.optJSONObject("data");
        boolean[] isDone={true};
        assert data != null;
        String mediaUrl=data.optString("link");

            DataBase.getInstance().MessageDao().updatePostLinkWithState(messageUid,mediaUrl, MessageStateEnum.SUCCESS.toString());
            try {
                Core.sendCtoS(DataBase.getInstance().MessageDao().getMessageWithUid(messageUid));
            } catch (JSONException e) {
                Log.d(TAG, "onSuccessCleanUp: json exception "+e.getMessage());
                isDone[0]=false;
            }



        return isDone[0];
    }

    private void updateProgress(String messageUid, long bytesUploaded, long totalBytes) {
       DataBase.getInstance().MessageDao().updateUploadProgress(messageUid,totalBytes,bytesUploaded);
    }

}
