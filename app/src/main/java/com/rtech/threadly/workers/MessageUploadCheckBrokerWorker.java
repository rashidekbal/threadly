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

          executor.execute(()->{
              List<MessageSchema> messageSchemas= DataBase.getInstance().MessageDao().getAllUnUploadedMessages(MessageStateEnum.UPLOADING.toString());
              for(MessageSchema schema : messageSchemas){
                  @SuppressLint("RestrictedApi") Data data=new Data.Builder().
                          put("path",schema.getMediaLocalPath())
                          .put("messageUid",schema.getMessageUid()).build();
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
