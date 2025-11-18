package com.rtech.threadly.models;

public class FollowRequestModel {
    private String username;
    private String userId;
    private String profilePic;
    private boolean isActionTaken;
    private String actionText="";

    public FollowRequestModel(String username, String userId, String profilePic, boolean isActionTaken, String actionText) {
        this.username = username;
        this.userId = userId;
        this.profilePic = profilePic;
        this.isActionTaken = isActionTaken;
        this.actionText = actionText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isActionTaken() {
        return isActionTaken;
    }

    public void setActionTaken(boolean actionTaken) {
        isActionTaken = actionTaken;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }
}
