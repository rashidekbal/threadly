package com.rtech.threadly.utils;

import static com.rtech.threadly.RoomDb.DataBase.getInstance;

import android.util.Log;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.constants.MessageStateEnum;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.MessageManager;
import com.rtech.threadly.network_managers.ProfileManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessengerUtils {
    //TODO consider special care for message with media which is sent
   private final  ExecutorService executor=Executors.newSingleThreadExecutor();
    public  void LoadAllChatsForLoginAction(){
        MessageManager.GetAllChatsAssociatedWithUser(new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray data=response.optJSONArray("data");
                if(data!=null&&data.length()>0){
                    OrganizeChats(data);
                }

            }
            @Override
            public void onError(String err) {
                LoggerUtil.log("MessageSyncError",err);

            }
        });

    }
    private  void OrganizeChats(JSONArray chats){
        String conversationId="";
        List<MessageSchema> messagesList=new ArrayList<>();
        for(int i=0;i<chats.length();i++){
            JSONObject chat=chats.optJSONObject(i);
                    int deliveryStatus=-5;
                    String otherParty="";
                    String messageUid=chat.optString("messageUid");
                    String replyToMessageId= chat.optString("replyToMessageId");
                    String senderUUId=chat.optString("senderUUId");
                    String recieverUUId=chat.optString("recieverUUId");
                    String type=chat.optString("type");
                    String message=chat.optString("message");
                    int postId=chat.optInt("postId");
                    String postLink=chat.optString("postLink");
                    String  creationTime=chat.optString("creationTime");

                    int isDeletedByReceiver= chat.optInt("isDeletedByReceiver");
                    int isDeletedBySender=chat.optInt("isDeletedBySender");
                    boolean isDeleted=false;
                    //check if the message to be stored on my device or not
                    if(senderUUId.equals(PreferenceUtil.getUUID())){
                        //if i am the sender
                        if(isDeletedBySender==1){
                            //and i deleted from my side
                            isDeleted=true;
                        }

                    }else{
                        //if i am the receiver
                        if(isDeletedByReceiver==1){
                            isDeleted=true;
                        }
                    }
                    //tweak delivery status accordingly
                    if(senderUUId.equals(PreferenceUtil.getUUID())){
                        //if i am the sender
                        deliveryStatus=chat.optInt("deliveryStatus");
                        otherParty=recieverUUId;

                    }else{
                        //if i am the receiver
                        otherParty=senderUUId;
                        //check if seen by me or not
                        if(chat.optInt("deliveryStatus")==3){
                            //set seen by me
                            deliveryStatus=-2;
                        } else{
                            //set not  seen by me;
                            deliveryStatus=-1;

                        }

                    }


                    if(i==0){
                        //for the first item the conversation id is generated directly;
                        conversationId=getConversationId(senderUUId,recieverUUId);
                        messagesList.add(new MessageSchema(messageUid,
                                conversationId,
                                replyToMessageId,
                                senderUUId,
                                recieverUUId,
                                message,
                                type,
                                postId,
                                postLink,
                                ReUsableFunctions.toIso8601Utc(creationTime),
                                deliveryStatus,
                                isDeleted,
                                null,
                                MessageStateEnum.SUCCESS.toString(),
                                0,
                                0
                                ));

                        if(chats.length()==1){
                            //only one chat

                            AddNewConversationHistory(otherParty);
                            insertMultiMessage(messagesList);

                        }
                    }

            if(i>0){
                if(!conversationId.equals(getConversationId(senderUUId,recieverUUId))){
                    //means new chat messages list has been started
                    //add existing data and clear the list

                    AddNewConversationHistory(otherParty);
                    insertMultiMessage(messagesList);
                    messagesList.clear();

                   //start fresh
                    conversationId=getConversationId(senderUUId,recieverUUId);

                }
                messagesList.add(new MessageSchema(messageUid,
                        conversationId,
                        replyToMessageId,
                        senderUUId,
                        recieverUUId,
                        message,
                        type,
                        postId,
                        postLink,
                        ReUsableFunctions.toIso8601Utc(creationTime),
                        deliveryStatus,
                        isDeleted,
                        null,
                        MessageStateEnum.SUCCESS.toString(),
                        0,
                        0
                ));

            }

            if(i==chats.length()-1){
                //for the last chat
                AddNewConversationHistory(otherParty);
                insertMultiMessage(messagesList);
            }

        }




    }

    public  void addMessageToDb(JSONObject object,String s_r_type){

        String ConversationId=object.optString(s_r_type.equals("s")?"receiverUuid":"senderUuid")+ Core.getPreference().getString(SharedPreferencesKeys.UUID, "null");
        String senderUuid=object.optString("senderUuid");
        String message =object.optString("message");
        String MessageUid=object.optString("MsgUid");
        String ReplyTOMessageUid=object.optString("ReplyTOMsgUid");
        String type=object.optString("type");
        String timestamp=object.optString("timestamp");
        int deliveryStatus=object.optInt("deliveryStatus");
        boolean isDeleted=object.optBoolean("isDeleted");
        int postId=object.optInt("postId");
        String postLink=object.optString("postLink");
       executor.execute(() -> getInstance().MessageDao().insertMessage(new MessageSchema(
                MessageUid,
                ConversationId,
                ReplyTOMessageUid,
                senderUuid,
                Core.getPreference().getString(SharedPreferencesKeys.UUID,null),
                message,
                type,
                postId,
                postLink,
                timestamp,
                deliveryStatus,
                isDeleted
        )));

    }

    public  void AddNewConversationHistory(String OtherPartyUuid) {

        String ConversationId = OtherPartyUuid + Core.getPreference().getString(SharedPreferencesKeys.UUID, "null");
        final HistorySchema[] history = {null};
executor.execute(()->{
    history[0] = DataBase.getInstance().historyOperator().getHistory(ConversationId);
});

        if (history[0] == null) {
            new ProfileManager().GetProfileByUuid(OtherPartyUuid, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                @Override
                public void onSuccess(JSONObject response) {
                    JSONArray Array = response.optJSONArray("data");
                    assert Array != null;
                    if (Array.length() > 0) {
                        JSONObject object = Array.optJSONObject(0);
                        String username = object.optString("username");
                        String userid = object.optString("userid");
                        String profile = object.optString("profilepic");
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                DataBase.getInstance().historyOperator().insertHistory(new HistorySchema(OtherPartyUuid + Core.getPreference().getString(SharedPreferencesKeys.UUID, "null")
                                        , username, userid, profile, OtherPartyUuid, "null",ReUsableFunctions.getTimestamp()));
                                Log.d("notfound", "data inserted ");
                            }
                        });


                    }

                }

                @Override
                public void onError(String err) {
                    Log.d("errorFetching", err);

                }
            });
        } else {
            //if history found update time stamp
            executor.execute(()->{
                String timeStamp=ReUsableFunctions.getTimestamp();
                DataBase.getInstance().historyOperator().updateTimeStamp(OtherPartyUuid +Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"),timeStamp);
            });

        }


    }

    public static String getConversationId(String senderUUID,String receiverUUID){
        if(senderUUID.equals(Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"))){
            //if sender is current user
            return receiverUUID+Core.getPreference().getString(SharedPreferencesKeys.UUID,"null");

        }else{
            //if current user is receiver
            return senderUUID+Core.getPreference().getString(SharedPreferencesKeys.UUID,"null");

        }
    }
    public  void insertMultiMessage(List<MessageSchema> messages){
        executor.execute(()->{
            DataBase.getInstance().MessageDao().insertMessage(messages);
        });
    }
    public void deleteMsg(String messageUid){
       executor.execute(()->{
            DataBase.getInstance().MessageDao().deleteMessage(messageUid);
        });
    }

}
