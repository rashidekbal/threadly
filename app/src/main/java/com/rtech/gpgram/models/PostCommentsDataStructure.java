package com.rtech.gpgram.models;

public class PostCommentsDataStructure {
    public int commentId,postId,likesCount;
    public boolean isLiked;
    public String userId,username,userDpUrl,comment,createdAt;

    public PostCommentsDataStructure(int commentId, int postId, int likesCount, int isLiked, String userId, String username, String userDpUrl, String comment, String createdAt){
        this.commentId=commentId;
        this.postId=postId;
        this.likesCount=likesCount;
        this.isLiked=isLiked>0;
        this.userId=userId;
        this.username=username;
        this.userDpUrl=userDpUrl;
        this.comment=comment;
        this.createdAt=createdAt;
    }
}
