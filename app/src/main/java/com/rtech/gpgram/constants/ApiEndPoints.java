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
    public static final String GET_FOLLOWERS=baseUrl+"/follow/getFollowers/";
    public static final String GET_FOLLOWINGS=baseUrl+"/follow/getFollowings/";

    // Posts
    public static final String GET_POST_BY_ID = baseUrl + "/posts/getPost/";
    public static final String GET_USER_POSTS = baseUrl+"/posts/getUserPosts/";

   // Profile
    public static final String GET_PROFILE = baseUrl + "/users/getUser/";

    // Authentication

    //otp
    public static final String SEND_MOBILE_OTP = baseUrl + "/otp/generateOtpMobile/";
    public static final String RESEND_MOBILE_OTP = baseUrl + "/otp/resendOtpMobile/";
    public static final String FORGET_PASSWORD_MOBILE_OTP = baseUrl + "/otp/ForgetPasswordGenerateOtpMobile";
    public static final String FORGET_PASSWORD_EMAIL_OTP = baseUrl + "/otp/forgetPasswordEmailOtp/";
    public static final String VERIFY_MOBILE_OTP = baseUrl + "/otp/verifyOtpMobile/";

    // Reset Password
    public static final String RESET_PASSWORD_MOBILE = baseUrl + "/resetPassword/Mobile/";
}
