package com.rtech.threadly.workers;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.constants.MessageStateEnum;
import com.rtech.threadly.core.Core;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageUploadCheckBrokerWorker extends Worker {
  ExecutorService executor=Executors.newSingleThreadExecutor();
    public MessageUploadCheckBrokerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
         CountDownLatch latch=new CountDownLatch(1);
         String[] messageUIds=getInputData().getStringArray("pendingUids");

          executor.execute(()->{
              for(String MessageUid: messageUIds){
                  MessageSchema schema=DataBase.getInstance().MessageDao().getMessageWithUid(MessageUid);
                  if(!schema.getMediaUploadState().equals(MessageStateEnum.UPLOADING.toString())){
                      continue;
                  }
                  Data data=new Data.Builder().putString("messageUid",schema.getMessageUid()).put("path",schema.getMediaLocalPath()).build();
                  Core.getWorkManager().enqueue(new OneTimeWorkRequest.Builder(MessageMediaHandlerWorker.class).setInputData(data).build());
              }
              latch.countDown();

          });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Result.success();

    }
}
