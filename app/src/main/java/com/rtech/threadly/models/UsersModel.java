package com.rtech.threadly.models;

public class UsersModel {
    private String uuid;
    private String username;
    private String userId;
    private String profilePic;

    public UsersModel(String uuid, String username, String userId, String profilePic) {
        this.uuid = uuid;
        this.username = username;
        this.userId = userId;
        this.profilePic = profilePic;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
