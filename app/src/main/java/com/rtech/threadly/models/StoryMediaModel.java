package com.rtech.threadly.models;

public class StoryMediaModel {
    private final String userId;
    private final int storyId;
    private final String storyUrl;
    private final boolean isVideo;
    private final String createdAt;
    private boolean isLiked;
    private boolean isViewed;
    private int viewCount;

    public StoryMediaModel(String userId, int storyId, String storyUrl, String type, String createdAt,int isLiked,int viewCount) {
        this.userId = userId;
        this.storyId = storyId;
        this.storyUrl = storyUrl;
        this.isVideo=type.equals("video");
        this.createdAt = createdAt;
        this.isLiked=isLiked>0;
        this.isViewed=false;
        this.viewCount=viewCount;

    }
    public StoryMediaModel(String userId, int storyId, String storyUrl, String type, String createdAt,int isLiked,int viewCount,int isViewed) {
        this.userId = userId;
        this.storyId = storyId;
        this.storyUrl = storyUrl;
        this.isVideo=type.equals("video");
        this.createdAt = createdAt;
        this.isLiked=isLiked>0;
        this.isViewed=false;
        this.viewCount=viewCount;
        this.isViewed=isViewed>0;

    }


    public String getUserId() {
        return userId;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    public boolean isViewed() {
        return isViewed;
    }
    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
