package com.rtech.gpgram.models;

public class Suggest_Profile_Model {
    public String userid;
    public String username;
    public String profilepic;
    public boolean isfollowedBy;
    public Suggest_Profile_Model(String userid, String username, String profilepic, int isfollowedBy){
        this.userid=userid;
        this.username=username;
        this.profilepic=profilepic;
        this.isfollowedBy=isfollowedBy>0;

    }
}
