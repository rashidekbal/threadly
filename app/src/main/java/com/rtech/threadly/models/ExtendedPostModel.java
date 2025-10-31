package com.rtech.threadly.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ExtendedPostModel extends Posts_Model implements Parcelable {

    public ExtendedPostModel(int CONTENT_TYPE, int postId, String userId, String username, String userDpUrl,
                             String postUrl, String caption, String createdAt, String likedBy,
                             int likeCount, int commentCount, int shareCount,
                             int isLiked, boolean isVideo, boolean isFollowed) {
        super(CONTENT_TYPE, postId, userId, username, userDpUrl, postUrl, caption, createdAt,
                likedBy, likeCount, commentCount, shareCount, isLiked, isVideo, isFollowed);
    }

    // 🧩 Constructor used when recreating object from Parcel
    protected ExtendedPostModel(Parcel in) {
        super(
                in.readInt(),              // CONTENT_TYPE
                in.readInt(),              // postId
                in.readString(),           // userId
                in.readString(),           // username
                in.readString(),           // userDpUrl
                in.readString(),           // postUrl
                in.readString(),           // caption
                in.readString(),           // createdAt
                in.readString(),           // likedBy
                in.readInt(),              // likeCount
                in.readInt(),              // commentCount
                in.readInt(),              // shareCount
                in.readInt(),              // isLiked
                in.readByte() != 0,        // isVideo
                in.readByte() != 0         // isFollowed
        );
    }

    // 🧱 Parcelable implementation
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getCONTENT_TYPE());
        dest.writeInt(getPostId());
        dest.writeString(getUserId());
        dest.writeString(getUsername());
        dest.writeString(getUserDpUrl());
        dest.writeString(getPostUrl());
        dest.writeString(getCaption());
        dest.writeString(getCreatedAt());
        dest.writeString(getLikedBy());
        dest.writeInt(getLikeCount());
        dest.writeInt(getCommentCount());
        dest.writeInt(getShareCount());
        dest.writeInt(getIsLiked()?1:0);
        dest.writeByte((byte) (isVideo() ? 1 : 0));
        dest.writeByte((byte) (isFollowed() ? 1 : 0));
    }

    private boolean getIsLiked() {
       return super.getIsliked();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExtendedPostModel> CREATOR = new Creator<ExtendedPostModel>() {
        @Override
        public ExtendedPostModel createFromParcel(Parcel in) {
            return new ExtendedPostModel(in);
        }

        @Override
        public ExtendedPostModel[] newArray(int size) {
            return new ExtendedPostModel[size];
        }
    };
}
