package com.rtech.gpgram.models;

public class ProfileDataStructure {
    public String userid;
    public String username;
    public String profilepic;
    public String bio;
    public String dob;
    public int followers;
    public int following;
    public int posts;
    public boolean isFollowing;

    public ProfileDataStructure(String userid, String username, String profilepic, String bio,String dob, int  followers, int following, int posts, int isFollowing) {
        this.userid = userid;
        this.username = username;
        this.profilepic = profilepic;
        this.bio = bio;
        this.dob=dob;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.isFollowing = isFollowing>0;
    }

}
