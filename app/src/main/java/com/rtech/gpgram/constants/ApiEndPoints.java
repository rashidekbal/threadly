package com.rtech.gpgram.constants;

import com.rtech.gpgram.BuildConfig;

public class ApiEndPoints {
    private static final String baseUrl = BuildConfig.BASE_URL;

    // Comments
    public static final String GET_COMMENTS = baseUrl + "/comment/getComments/";
    public static final String ADD_COMMENT = baseUrl + "/comment/addComment/";

    // Likes
    public static final String LIKE_POST = baseUrl + "/like/likePost/";
    public static final String UNLIKE_POST = baseUrl + "/like/unlikePost/";
    public static final String LIKE_COMMENT = baseUrl + "/like/likeAComment/";
    public static final String UNLIKE_COMMENT = baseUrl + "/like/unlikeAComment/";

    // Follows
    public static final String FOLLOW = baseUrl + "/follow/follow";
    public static final String UNFOLLOW = baseUrl + "/follow/unfollow";

    // Posts
    public static final String GET_POST_BY_ID = baseUrl + "/posts/getPost/";
}
