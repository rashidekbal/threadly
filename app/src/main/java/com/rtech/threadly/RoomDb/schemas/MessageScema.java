package com.rtech.threadly.RoomDb.schemas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "messages")
public class MessageScema {
    @PrimaryKey(autoGenerate = true)
    private long msgId;
    @ColumnInfo(name = "conversationId")
    private long conversationId;
    @ColumnInfo(name = "replyToMsgId")
    private  long replyToMsgId;
    @ColumnInfo(name="senderId")
    private String senderId;
    @ColumnInfo(name="receiverId")
    private String receiverId;
    @ColumnInfo(name="msg")
    private String msg;
    @ColumnInfo(name="type")
    private String type;
    @ColumnInfo(name="timestamp")
    private  String timestamp;
    @ColumnInfo(name="deliveryStatus")
    private int deliveryStatus;
    @ColumnInfo(name = "isDeleted")
    private boolean isDeleted;

    public MessageScema(long msgId, long conversationId, long replyToMsgId, String senderId, String receiverId, String msg, String type, String timestamp, int deliveryStatus, boolean isDeleted) {
        this.msgId = msgId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public long getReplyToMsgId() {
        return replyToMsgId;
    }

    public void setReplyToMsgId(long replyToMsgId) {
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
}
