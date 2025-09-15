package com.rtech.threadly.SocketIo;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketEmitterEvents {
    public static void UpdateSeenMsg_status(String senderUUid,String userid)throws JSONException {
        JSONObject object=new JSONObject();
        object.put("senderUUid",senderUUid);
        object.put("myUserid",userid);
        SocketManager.getInstance().getSocket().emit("update_seen_msg_status",object);
    }
}
