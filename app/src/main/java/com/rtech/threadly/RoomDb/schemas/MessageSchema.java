package com.rtech.threadly.RoomDb.schemas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "messages")
public class MessageSchema {
    @PrimaryKey(autoGenerate = true)
    private long msgId;
    @ColumnInfo(name = "messageUid")
    private String messageUid;
    @ColumnInfo(name = "conversationId")
    private String conversationId;
    @ColumnInfo(name = "replyToMsgId")
    private  String replyToMsgId;
    @ColumnInfo(name="senderId")
    private String senderId;
    @ColumnInfo(name="receiverId")
    private String receiverId;
    @ColumnInfo(name="msg")
    private String msg;
    @ColumnInfo(name="type")
    private String type;
    @ColumnInfo(name="postId")
    private int  postId;
    @ColumnInfo(name = "postLink")
    private String postLink;
    @ColumnInfo(name="timestamp")
    private  String timestamp;
    @ColumnInfo(name="deliveryStatus")
    private int deliveryStatus;
    @ColumnInfo(name = "isDeleted")
    private boolean isDeleted;


    public MessageSchema(long msgId, String messageUid, String conversationId, String replyToMsgId, String senderId, String receiverId, String msg, String type, String timestamp, int deliveryStatus, boolean isDeleted) {
        this.msgId = msgId;
        this.messageUid = messageUid;
        this.conversationId = conversationId;
        this.replyToMsgId = replyToMsgId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.type = type;
        this.timestamp = timestamp;
        this.deliveryStatus = deliveryStatus;
        this.isDeleted = isDeleted;
    }
    @Ignore

    public MessageSchema(String messageUid, String conversationId, String replyToMsgId, String senderId, String receiverId, String msg, String type,int postId,String postLink ,String timestamp, int deliveryStatus, boolean isDeleted) {
        this.messageUid = messageUid;
        this.conversationId = conversationId;
        this.replyToMsgId = replyToMsgId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.type = type;
        this.timestamp = timestamp;
        this.deliveryStatus = deliveryStatus;
        this.isDeleted = isDeleted;
        this.postId=postId;
        this.postLink=postLink;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getReplyToMsgId() {
        return replyToMsgId;
    }

    public void setReplyToMsgId(String replyToMsgId) {
        this.replyToMsgId = replyToMsgId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    public int  getPostId(){
        return postId;
    }
    public void setPostId(int pid){
        postId=pid;
    }
    public void setPostLink(String pl){
        postLink=pl;
    }
    public String getPostLink(){return postLink;}
}
