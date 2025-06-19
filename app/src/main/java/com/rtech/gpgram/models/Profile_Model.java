package com.rtech.gpgram.models;

public class Profile_Model {
    public String userid;
    public String username;
    public String profilepic;
    public String bio;
    public String dob;
    public int followers;
    public int following;
    public int posts;
    public boolean isFollowingMe;
    public boolean isFollowedByMe;

    public Profile_Model(String userid, String username, String profilepic, String bio, String dob, int  followers, int following, int posts, int isFollowedByMe, int isFollowingMe) {
        this.userid = userid;
        this.username = username;
        this.profilepic = profilepic;
        this.bio = bio;
        this.dob=dob;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.isFollowedByMe = isFollowedByMe>0;
        this.isFollowingMe= isFollowingMe>0;


    }

}
