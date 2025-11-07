package com.rtech.threadly.models;

public class Posts_Comments_Model {
    public int commentId,postId,likesCount;
    public boolean isLiked;
    private int replyCount;
    public String userId,username,userDpUrl,comment,createdAt;

    public Posts_Comments_Model(int commentId, int postId, int likesCount, int isLiked, String userId, String username, String userDpUrl, String comment, String createdAt){
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
    public Posts_Comments_Model(int commentId, int postId, int likesCount, int isLiked, String userId, String username, String userDpUrl, String comment, String createdAt,int replyCount){
        this.commentId=commentId;
        this.postId=postId;
        this.likesCount=likesCount;
        this.isLiked=isLiked>0;
        this.userId=userId;
        this.username=username;
        this.userDpUrl=userDpUrl;
        this.comment=comment;
        this.createdAt=createdAt;
        this.replyCount=replyCount;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserDpUrl() {
        return userDpUrl;
    }

    public void setUserDpUrl(String userDpUrl) {
        this.userDpUrl = userDpUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
