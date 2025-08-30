package com.rtech.threadly.RoomDb.schemas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "UsersHistory")
public class HistorySchema  {
    @ColumnInfo(name =
    "userid")
    private final String userId;
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "conversationId")
    private String conversationId;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "profilePic")
    private String profilePic;
    @ColumnInfo(name="uuid")
    private String uuid;
    @ColumnInfo(name="latestMsg")
    private String msg;

    public HistorySchema(int id, String conversationId, String username,String userId, String profilePic, String uuid, String msg) {
        this.id = id;
        this.conversationId = conversationId;
        this.username = username;
        this.userId=userId;
        this.profilePic = profilePic;
        this.uuid = uuid;
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    @Ignore
    public HistorySchema(String conversationId, String username,String userId, String profilePic, String uuid, String msg) {
        this.conversationId = conversationId;
        this.username = username;
        this.profilePic = profilePic;
        this.uuid = uuid;
        this.userId=userId;
        this.msg = msg;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
