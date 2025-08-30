package com.rtech.threadly.models;

public class Posts_Model {
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
            (int postId,
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
}
