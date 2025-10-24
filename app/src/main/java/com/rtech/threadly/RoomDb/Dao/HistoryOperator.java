package com.rtech.threadly.RoomDb.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rtech.threadly.RoomDb.schemas.HistorySchema;

import java.util.List;

@Dao
public interface HistoryOperator {
    @Insert
    void insertHistory(HistorySchema history);
    @Query("select * from UsersHistory group by conversationId order by timestamp desc")
    LiveData<List<HistorySchema>> getAllHistory();
    @Query("select * from UsersHistory where conversationId=:conversationId group by conversationId")
    HistorySchema getHistory(String conversationId);
    @Query("update UsersHistory set timeStamp=:timeStamp where conversationId=:conversationId")
    void updateTimeStamp(String conversationId,String timeStamp);
}
