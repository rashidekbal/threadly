package com.rtech.threadly.utils;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.constants.MessageStateEnum;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationPageManagerUtil {
    private static final ExecutorService executor= Executors.newSingleThreadExecutor();
    public static void clearFollowRequest(){
        executor.execute(()->{
            DataBase.getInstance().notificationDao().deleteAllNotificationsOfType(Constants.FOLLOW_REQUEST_NOTIFICATION.toString());
        });

    }
}
