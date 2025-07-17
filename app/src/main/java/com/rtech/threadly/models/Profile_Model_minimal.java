package com.rtech.threadly.models;

public class Profile_Model_minimal {
    public String userid;
    public String username;
    public String profilepic;
    public boolean isfollowedBy;
    public Profile_Model_minimal(String userid, String username, String profilepic, int isfollowedBy){
        this.userid=userid;
        this.username=username;
        this.profilepic=profilepic;
        this.isfollowedBy=isfollowedBy>0;

    }
}
