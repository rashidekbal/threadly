package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;


import java.util.List;

public class InteractionNotificationViewModel extends AndroidViewModel {
    public InteractionNotificationViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<NotificationSchema>> getInteractionNotification(){
        return DataBase.getInstance().notificationDao().getNotification();
    }
    public LiveData<Integer> getPendingNotificationCount(){
        return DataBase.getInstance().notificationDao().getUnseenNotificationCount();
    }
    public LiveData<Integer> getUnInteractedRequestCount(){
        return DataBase.getInstance().notificationDao().getUnInteractedRequestCount();
    }
}
