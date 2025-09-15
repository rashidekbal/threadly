package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;

import java.util.List;

public class MessagesViewModel extends AndroidViewModel {
    public MessagesViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<MessageSchema>> getMessages(String conversationId){
        return DataBase.getInstance().dao().getMessagesCid(conversationId);
    }
    public LiveData<Integer> getUnreadMsg_count(String userUUid){
        return DataBase.getInstance().dao().getUnreadMessagesCount(userUUid);

    }
    public LiveData<Integer> getConversationUnreadMsg_count(String conversationId,String userUUid){
        return DataBase.getInstance().dao().getConversationUnreadMessagesCount(conversationId,userUUid);}
}
