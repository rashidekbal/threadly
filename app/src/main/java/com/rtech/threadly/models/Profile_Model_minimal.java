package com.rtech.threadly.models;

public class Profile_Model_minimal {
    public String userid;
    public String username;
    public String profilepic;
    public boolean isfollowedBy;
    private boolean isPrivate;
    private boolean isApproved;
    public Profile_Model_minimal(String userid, String username, String profilepic, int isfollowedBy){
        this.userid=userid;
        this.username=username;
        this.profilepic=profilepic;
        this.isfollowedBy=isfollowedBy>0;

    }
    public Profile_Model_minimal(String userid, String username, String profilepic, int isfollowedBy,boolean isPrivate,boolean isApproved){
        this.userid=userid;
        this.username=username;
        this.profilepic=profilepic;
        this.isfollowedBy=isfollowedBy>0;
        this.isPrivate=isPrivate;
        this.isApproved=isApproved;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
