package com.rtech.threadly.models;

public class PostLiked_UserModel extends UsersModel{
    private final boolean isLiked;

    public PostLiked_UserModel(String uuid, String username, String userId, String profilePic,int isLikedBy) {
        super(uuid, username, userId, profilePic);
        this.isLiked=isLikedBy>0;
    }

    public boolean isLiked() {
        return isLiked;
    }
}
