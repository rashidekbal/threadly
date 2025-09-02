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
    @Query("select * from messages where conversationId=:conversationId order by timestamp asc")
    LiveData<List<MessageSchema>> getMessagesCid(String conversationId);


}
