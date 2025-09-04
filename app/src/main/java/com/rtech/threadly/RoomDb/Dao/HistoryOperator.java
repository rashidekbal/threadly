package com.rtech.threadly.RoomDb.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rtech.threadly.RoomDb.schemas.HistorySchema;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface HistoryOperator {
    @Insert
    void insertHistory(HistorySchema history);
    @Query("select * from UsersHistory")
    LiveData<List<HistorySchema>> getAllHistory();
    @Query("select * from UsersHistory where conversationId=:conversationId group by conversationId")
    HistorySchema getHistory(String conversationId);
}
