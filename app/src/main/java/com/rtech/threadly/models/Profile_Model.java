package com.rtech.threadly.models;

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
    private boolean isPrivate;
    private boolean isFollowRequestApproved;

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
    public Profile_Model(String userid, String username, String profilepic, String bio, String dob, int  followers, int following, int posts, int isFollowedByMe, int isFollowingMe,boolean isPrivate,boolean isFollowRequestApproved) {
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
        this.isPrivate=isPrivate;
        this.isFollowRequestApproved=isFollowRequestApproved;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public boolean isFollowingMe() {
        return isFollowingMe;
    }

    public void setFollowingMe(boolean followingMe) {
        isFollowingMe = followingMe;
    }

    public boolean isFollowedByMe() {
        return isFollowedByMe;
    }

    public void setFollowedByMe(boolean followedByMe) {
        isFollowedByMe = followedByMe;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isFollowRequestApproved() {
        return isFollowRequestApproved;
    }

    public void setFollowRequestApproved(boolean followRequestApproved) {
        isFollowRequestApproved = followRequestApproved;
    }
}
