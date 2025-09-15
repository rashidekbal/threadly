package com.rtech.threadly.RoomDb.schemas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_schema")
public class NotificationSchema {
    @PrimaryKey (autoGenerate = true)
    int notificationId;

}
