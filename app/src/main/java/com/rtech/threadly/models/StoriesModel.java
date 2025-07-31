package com.rtech.threadly.models;

public class StoriesModel {
    public String userid;
    public String userProfile;
    public boolean isSeen;

    public StoriesModel(String userid, String userProfile,boolean isSeen) {
        this.userid = userid;
        this.userProfile = userProfile;
        this.isSeen=isSeen;
    }
}
