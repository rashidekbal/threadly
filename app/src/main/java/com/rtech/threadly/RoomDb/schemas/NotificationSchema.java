package com.rtech.threadly.RoomDb.schemas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.NavigableMap;

@Entity(tableName = "notification_schema")
public class NotificationSchema {
    @PrimaryKey (autoGenerate = true)
    int notificationId;
    @ColumnInfo(name="notificationType")
    String notificationType;
    @ColumnInfo(name="insertId")
    int insertId;
    @ColumnInfo(name="userId")
    String userId;
    @ColumnInfo(name = "username")
    String username;
    @ColumnInfo(name="profilePic")
    String profilePic;
    @ColumnInfo(name="postId")
    int postId;
    @ColumnInfo(name = "postLink")
    String postLink;
    @ColumnInfo(name = "isFollowed")
    boolean isFollowed;
    @ColumnInfo(name = "isViewed")
    boolean isViewed;

    public NotificationSchema(int notificationId, String notificationType, int insertId, String username, String userId, String profilePic, int postId, String postLink, boolean isFollowed, boolean isViewed) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.insertId = insertId;
        this.username = username;
        this.userId = userId;
        this.profilePic = profilePic;
        this.postId = postId;
        this.postLink = postLink;
        this.isFollowed = isFollowed;
        this.isViewed = isViewed;
    }

    @Ignore
    public NotificationSchema(String notificationType, int insertId, String userId, String profilePic, String username, int postId, String postLink, boolean isFollowed,boolean isViewed) {
        this.notificationType = notificationType;
        this.insertId = insertId;
        this.userId = userId;
        this.profilePic = profilePic;
        this.username = username;
        this.postId = postId;
        this.postLink = postLink;
        this.isFollowed = isFollowed;
        this.isViewed=isViewed;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public int getInsertId() {
        return insertId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getPostId() {
        return postId;
    }

    public String getPostLink() {
        return postLink;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }
}
