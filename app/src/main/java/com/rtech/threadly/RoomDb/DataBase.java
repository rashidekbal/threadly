package com.rtech.threadly.RoomDb;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rtech.threadly.RoomDb.Dao.HistoryOperator;
import com.rtech.threadly.RoomDb.Dao.operator;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.Threadly;

@Database(entities = {MessageSchema.class, HistorySchema.class},version = 1,exportSchema = false)
public abstract class DataBase extends RoomDatabase {
    public static final String DB_NAME="Threadly";
    public static DataBase instance ;

    public static synchronized DataBase getInstance(){
        if (instance==null){
            instance= Room.databaseBuilder(Threadly.getGlobalContext(),DataBase.class,DB_NAME).build();
        }
        return instance;
    }
    public abstract operator dao();
    public abstract HistoryOperator historyOperator();


}
