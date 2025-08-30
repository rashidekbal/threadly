package com.rtech.threadly.RoomDb.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rtech.threadly.RoomDb.schemas.HistorySchema;

import java.util.ArrayList;

@Dao
public interface HistoryOperator {
    @Insert
    void insertHistory(HistorySchema history);
    @Query("select * from UsersHistory")
    HistorySchema[] getAllHistory();
    @Query("select * from UsersHistory where conversationId=:conversationId")
    HistorySchema getHistory(String conversationId);
}
