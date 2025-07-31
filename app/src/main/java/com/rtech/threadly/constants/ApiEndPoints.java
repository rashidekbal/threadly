package com.rtech.threadly.constants;

import com.rtech.threadly.BuildConfig;

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
    public static final String FOLLOW = baseUrl + "/follow/follow/";
    public static final String UNFOLLOW = baseUrl + "/follow/unfollow/";
    public static final String GET_FOLLOWERS=baseUrl+"/follow/getFollowers/";
    public static final String GET_FOLLOWINGS=baseUrl+"/follow/getFollowings/";

    // Posts
    public static final String GET_POST_BY_ID = baseUrl + "/posts/getPost/";
    public static final String GET_USER_POSTS = baseUrl+"/posts/getUserPosts/";
    public static final String GET_IMAGE_FEED = baseUrl+"/posts/getImagePostsFeed/";
    public static final String GET_VIDEO_FEED = baseUrl+"/posts/getVideoPostsFeed/";
    public static final String ADD_IMAGE_POST = baseUrl+"/posts/addImagePost/";
    public static final String ADD_VIDEO_POST = baseUrl+"/posts/addVideoPost/";

   // Profile
    public static final String GET_PROFILE = baseUrl + "/users/getUser/";
    public static final String GET_LOGGED_IN_USER_PROFILE = baseUrl + "/users/getMyData/";

    // Authentication

    //otp mobile
    public static final String SEND_MOBILE_OTP = baseUrl + "/otp/generateOtpMobile/";
    public static final String RESEND_MOBILE_OTP = baseUrl + "/otp/resendOtpMobile/";
    public static final String FORGET_PASSWORD_MOBILE_OTP = baseUrl + "/otp/ForgetPasswordGenerateOtpMobile";
    public static final String FORGET_PASSWORD_EMAIL_OTP = baseUrl + "/otp/ForgetPasswordGenerateOtpEmail/";
    public static final String VERIFY_MOBILE_OTP = baseUrl + "/otp/verifyOtpMobile/";

    //otp email
    public static final String SEND_EMAIL_OTP=baseUrl+"/otp/generateOtpEmail/";
    public static final String VERIFY_EMAIL_OTP=baseUrl+"/otp/verifyOtpEmail/";

    // Reset Password
    public static final String RESET_PASSWORD_MOBILE = baseUrl + "/resetPassword/Mobile/";
    public static final String RESET_PASSWORD_EMAIL = baseUrl + "/resetPassword/Email/";

    // register route
    public static final String REGISTER_MOBILE = baseUrl + "/auth/register/mobile/";
    public static final String REGISTER_EMAIL = baseUrl + "/auth/register/email/";

    //login route
    public static final String LOGIN_MOBILE = baseUrl + "/auth/login/mobile/";
    public static final String LOGIN_EMAIL = baseUrl + "/auth/login/email/";
    public static final String LOGIN_USERID = baseUrl + "/auth/login/userid/";

    //Edit userDetails
    public static final String EDIT_USERNAME = baseUrl + "/profile/edit/username/";
    public static final String EDIT_USERID =baseUrl + "/profile/edit/userid/";
    public static final String EDIT_BIO = baseUrl + "/profile/edit/bio/";
    public static final String EDIT_PROFILE_PICTURE = baseUrl +"/profile/edit/profile";

    // story routes
    public static String ADD_STORY = baseUrl + "/story/addStory/";
    public static String GET_STORIES = baseUrl + "/story/getStories/";


}
