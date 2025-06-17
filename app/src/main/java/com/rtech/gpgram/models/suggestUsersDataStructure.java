package com.rtech.gpgram.models;

public class suggestUsersDataStructure {
    public String userid;
    public String username;
    public String profilepic;
    public boolean isfollowedBy;
    public suggestUsersDataStructure(String userid,String username,String profilepic,int isfollowedBy){
        this.userid=userid;
        this.username=username;
        this.profilepic=profilepic;
        this.isfollowedBy=isfollowedBy>0;

    }
}
