package com.rtech.threadly.models;

public class Posts_Model {
    /// here CTYPE or CONTENT_TYPE is a variable which is
    /// used to denote weather a given item
    /// is of type suggested
    /// or content
    /// if the item is a content then it must be 0
    /// else if the item is of type suggestion then the code must be 1
    ///
    /// due to some flaw in coding understanding all the variable are public convert to private one by one please
    ///
    public int CONTENT_TYPE;
   public int postId,likeCount,commentCount,shareCount;
   public String userId;
   public  String username,likedBy;
   public String postUrl;
   public String caption;
   public String createdAt;
    public String userDpUrl;
   public Boolean isliked;
   public boolean isVideo,isFollowed;
    public Posts_Model
            (int CONTENT_TYPE,
             int postId,
             String userId,
             String username,
             String userDpUrl,
             String postUrl,
             String caption,
             String createdAt,
             String likedBy,
             int likeCount,
             int commentCount,
             int shareCount ,
             int isLiked,
            boolean isVideo,
            boolean isFollowed){
        this.CONTENT_TYPE=CONTENT_TYPE;
        this.postId=postId;
        this.userId=userId;
        this.username=username;
        this.userDpUrl=userDpUrl;
        this.postUrl=postUrl;
        this.caption=caption;
        this.createdAt=createdAt;
        this.likeCount=likeCount;
        this.commentCount=commentCount;
        this.shareCount=shareCount;
        this.isliked= isLiked>0;
        this.likedBy=likedBy;
        this.isVideo=isVideo;
        this.isFollowed=isFollowed;

    }

    public int getCONTENT_TYPE() {
        return CONTENT_TYPE;
    }

    public void setCONTENT_TYPE(int CONTENT_TYPE) {
        this.CONTENT_TYPE = CONTENT_TYPE;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(String likedBy) {
        this.likedBy = likedBy;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserDpUrl() {
        return userDpUrl;
    }

    public void setUserDpUrl(String userDpUrl) {
        this.userDpUrl = userDpUrl;
    }

    public Boolean getIsliked() {
        return isliked;
    }

    public void setIsliked(Boolean isliked) {
        this.isliked = isliked;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
