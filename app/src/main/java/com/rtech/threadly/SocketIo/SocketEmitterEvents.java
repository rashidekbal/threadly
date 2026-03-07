package com.rtech.threadly.SocketIo;

import android.util.Log;

import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SocketEmitterEvents {
    public static void UpdateSeenMsg_status(List<String> MessageUids, String senderUUid, String userid)
            throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray UidsList = new JSONArray();
        for (String uid : MessageUids) {
            UidsList.put(uid);
        }
        object.put("senderUUid", senderUUid);
        object.put("myUserid", userid);
        object.put("uids", UidsList);

        SocketManager.getInstance().getSocket().emit("update_seen_msg_status", object);
    }
    public static void emitPostViewed(int postId){
        JSONObject object=new JSONObject();
        try {
            object.put("uuid", PreferenceUtil.getUUID());
            object.put("userid",PreferenceUtil.getUserId());
            object.put("postid",postId);
            if(!SocketManager.getInstance().getSocket().connected()){
                PostsManager.markPostViewed(postId, object, new NetworkCallbackInterfaceJsonObject() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d("postViewed", "onSuccess: form http");
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.d("postViewed", "onError: form http");

                    }
                });
                return;

            }
            SocketManager.getInstance().getSocket().emit("postViewed",object);
        } catch (JSONException e) {
           return;
        }

    }


}
