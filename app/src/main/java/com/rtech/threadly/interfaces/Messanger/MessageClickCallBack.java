package com.rtech.threadly.interfaces.Messanger;

import com.rtech.threadly.RoomDb.schemas.MessageSchema;

public interface MessageClickCallBack {
    void onItemClicked(MessageSchema messageSchema,String type);
}
