package com.rtech.threadly.SocketIo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SocketEmitterEvents {
    public static void UpdateSeenMsg_status(List<String> MessageUids, String senderUUid, String userid)throws JSONException {
        JSONObject object=new JSONObject();
        JSONArray UidsList=new JSONArray();
        for(String uid:MessageUids){
            UidsList.put(uid);
        }
        object.put("senderUUid",senderUUid);
        object.put("myUserid",userid);
        object.put("uids",UidsList);

        SocketManager.getInstance().getSocket().emit("update_seen_msg_status",object);
    }
}
