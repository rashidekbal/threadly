package com.rtech.threadly.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;

import java.util.List;

public class UsersMessageHistoryProfileViewModel extends AndroidViewModel {
    public UsersMessageHistoryProfileViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<HistorySchema>> getHistory(){
        return DataBase.getInstance().historyOperator().getAllHistory();
    }
}
