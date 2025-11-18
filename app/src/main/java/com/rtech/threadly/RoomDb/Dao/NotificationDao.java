package com.rtech.threadly.RoomDb.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rtech.threadly.RoomDb.schemas.NotificationSchema;


import java.util.List;

@Dao
public interface NotificationDao {
    @Query("select * from notification_schema where not notificationType='FOLLOW_REQUEST_NOTIFICATION' order by notificationId desc")
    LiveData<List<NotificationSchema>> getNotification();
    @Insert
    void addNotification(NotificationSchema schema);
    @Query("delete from notification_schema where insertId=:insertId")
    void removeNotificationHistory(int insertId);
    @Query("select count(distinct ns.insertId)as notificationCount from notification_schema as ns where isViewed=0 group by notificationId")
    LiveData<Integer> getUnseenNotificationCount();
    @Query("update notification_schema set isViewed=1 ")
    void markAllNotificationsAsViewed();
    @Query("delete from notification_schema where userId=:userId and postId=:postId")
    void deletePostLikeNotification(String userId,int postId);
    @Query("delete from notification_schema where userId=:userId and notificationType=:notificationType")
    void deleteFollowNotification(String userId,String notificationType);
    @Query("update notification_schema set isFollowed=:state where notificationId=:notificationId")
    void markedFollowState(int state,int notificationId);
    @Query("delete from notification_schema where userId=:userId and commentId=:commentId")
    void deleteCommentLikeNotification(String userId,int commentId);
    @Query("update notification_schema set isApproved=:isApproved where notificationType=:notificationType and userId=:userid")
    void markFollowApprovalStatus(boolean isApproved,String notificationType,String userid);
    @Query("delete from notification_schema where notificationType=:notificationType")
    void deleteAllNotificationsOfType(String notificationType);
    @Query("select count(distinct ns.insertId)as requestCount  from notification_schema as ns where isApproved=0 and  notificationType='FOLLOW_REQUEST_NOTIFICATION' order by notificationId desc")
    LiveData<Integer> getUnInteractedRequestCount();

}
