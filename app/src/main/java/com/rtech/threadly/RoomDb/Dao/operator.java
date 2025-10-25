package com.rtech.threadly.RoomDb.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rtech.threadly.RoomDb.schemas.MessageSchema;

import java.util.List;

@Dao
public interface operator {
    @Insert
    void insertMessage(MessageSchema message);
    @Insert
    void insertMessage(List<MessageSchema> messages);

    @Query("select * from messages where conversationId=:conversationId  order by timestamp asc")
    LiveData<List<MessageSchema>> getMessagesCid(String conversationId);

    @Query("update messages set deliveryStatus=:deliveryStatus  where messageUid=:msgUid")
    void updateDeliveryStatus(String msgUid,int deliveryStatus);

    @Query("select * from messages where deliveryStatus=0")
    List<MessageSchema> getPendingToSendMessages();

    @Query("select count(distinct messageUid)as count from messages where deliveryStatus=-1 and receiverId=:rid and isDeleted=0" )
    LiveData<Integer> getUnreadMessagesCount(String rid);

    @Query("select count(distinct conversationId)as count from messages where deliveryStatus=-1 and receiverId=:rid and isDeleted=0" )
    LiveData<Integer> getUnreadConversationCount(String rid);

    @Query("update messages set deliveryStatus=-2 where conversationId=:conversationId and receiverId=:rid")
    void updateMessagesSeen(String conversationId,String rid);

    @Query("select count(distinct messageUid) from messages where deliveryStatus=-1 and receiverId=:rid and conversationId=:cid")
    LiveData<Integer> getConversationUnreadMessagesCount(String cid,String rid);

    @Query("update messages set isDeleted=1 where messageUid=:msgUid")
    void deleteMessage(String msgUid);




}
