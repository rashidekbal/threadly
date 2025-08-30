package com.rtech.threadly.RoomDb.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.RoomDb.schemas.MessageScema;

@Dao
public interface operator {
    @Insert
    void insertMessage(MessageScema message);
    @Query("select * from messages where conversationId=:conversationId")
    MessageScema getMessagesCid(long conversationId);
    @Query("select * from messages group by conversationId ")
    MessageScema[] getAllCid();

}
