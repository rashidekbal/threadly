package com.rtech.threadly.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.constants.FollowRouteResponse;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityUserProfileBinding;
import com.rtech.threadly.fragments.CustomPostFeed.CustomPostFeedFragment;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.network_managers.ProfileManager;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.utils.PreferenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding mainXml;
    ProfileManager profileManager;
    Intent intentData;
    Profile_Model profileData;
    GridPostAdapter postAdapter;
    ArrayList<Posts_Model> postsArray=new ArrayList<>();
    GridLayoutManager layoutManager;
    PostsManager postsManager;
    FollowManager followManager;
    SharedPreferences loginInfo;
    boolean isLoading=false;
    boolean isLastPage=false;
    private final int threshold=20;
    int page=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainXml=ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // initialize all ids
        init();
        //get profile data
        getProfileData();

       mainXml.swipeRefresh.setOnRefreshListener(this::getProfileData);
        //on click listeners for followers and following text views
        mainXml.followersCountText.setOnClickListener(v -> {
            Intent intent=new Intent(UserProfileActivity.this, FollowerFollowingList.class);
            intent.putExtra("type","followers");
            intent.putExtra("userid",intentData.getStringExtra("userid"));
            startActivity(intent);
        });
        mainXml.followingCountText.setOnClickListener(v -> {
            Intent intent=new Intent(UserProfileActivity.this, FollowerFollowingList.class);
            intent.putExtra("type","following");
            intent.putExtra("userid",intentData.getStringExtra("userid"));
            startActivity(intent);
        });
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if(getSupportFragmentManager().getBackStackEntryCount()==0){
                mainXml.postsFrameLayout.setVisibility(View.GONE);
            }
        });




    }

    private void init(){
        profileManager=new ProfileManager();
        postsManager=new PostsManager();
        followManager=new FollowManager();
        intentData=getIntent();

        loginInfo= Core.getPreference();

        layoutManager=new GridLayoutManager(this,3);
        postAdapter=new GridPostAdapter(UserProfileActivity.this, postsArray, new Post_fragmentSetCallback() {

            @Override
            public void openPostFragment(ArrayList<Posts_Model> postsArray, int position) {
                /// add fragment system to open  all posts of a given profile
                mainXml.postsFrameLayout.setVisibility(View.VISIBLE);
                ArrayList<ExtendedPostModel> postArrayList=new ArrayList<>();
                for(Posts_Model model:postsArray){
                    postArrayList.add(new ExtendedPostModel(model.getCONTENT_TYPE(),
                            model.getPostId(),
                            model.getUserId(),
                            model.getUsername(),
                            model.getUserDpUrl(),
                            model.getPostUrl(),
                            model.getCaption(),
                            model.getCreatedAt(),
                            model.getLikedBy(),
                            model.getLikeCount(),
                            model.getCommentCount(),
                            model.getShareCount(),
                            model.getIsliked()?1:0,
                            model.isVideo(),
                            model.isFollowed()));
                }
                CustomPostFeedFragment customPostFeedFragment=new CustomPostFeedFragment();
                Bundle data=new Bundle();
                data.putParcelableArrayList("postList",postArrayList);
                data.putInt("position",position);
                customPostFeedFragment.setArguments(data);
                getSupportFragmentManager().beginTransaction().replace(mainXml.postsFrameLayout.getId(),customPostFeedFragment).addToBackStack(null).commit();



            }

            @Override
            public void openEditor() {

            }
        });
        postAdapter.setHasStableIds(true);
        mainXml.postsRecyclerView.setLayoutManager(layoutManager);
        mainXml.postsRecyclerView.setAdapter(postAdapter);
        mainXml.postsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    int currentVisibleCount=layoutManager.getChildCount();
                    int FirstVisibleCount=layoutManager.findFirstVisibleItemPosition();
                    int totalCount=layoutManager.getItemCount();
                    if(currentVisibleCount+FirstVisibleCount>=totalCount-threshold){
                        loadMore();
                    }
                }
            }
        });





    }


    private void getProfileData(){
        profileManager.GetProfile(intentData.getStringExtra("userid"), new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {

                mainXml.profileShimmer.setVisibility(ShimmerFrameLayout.GONE);
                mainXml.profileLayout.setVisibility(LinearLayout.VISIBLE);
                try {
                    JSONArray array=response.getJSONArray("data");
                    JSONObject object=array.getJSONObject(0);
                    profileData=new Profile_Model(
                            object.getString("userid")
                            ,object.getString("username")
                            ,object.getString("profilepic")
                            ,object.getString("bio")
                            ,"null"
                            ,object.getInt("Followers")
                            ,object.getInt("Following")
                            ,object.getInt("Posts")
                            ,object.getInt("isFollowedByUser"),
                            object.getInt("isFollowingUser"),
                            object.getInt("isPrivate")==1,
                            object.optInt("isApproved") == 1);

                    setData(profileData);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }



            }

            @Override
            public void onError(int errorCode) {
                Log.d("error_loadingProfile", Integer.toString(errorCode));

            }
        });

    }

    private void setData(Profile_Model data){
        mainXml.followBtn.setVisibility(View.GONE);
        mainXml.requestCancelBtn.setVisibility(View.GONE);
        mainXml.unfollowBtn.setVisibility(View.GONE);
        mainXml.swipeRefresh.setRefreshing(false
        );
        mainXml.useridText.setText(data.userid);
        mainXml.usernameText.setText(data.username);
        mainXml.postsCountText.setText(String.valueOf(data.posts));
        mainXml.followersCountText.setText(String.valueOf(data.followers));
        mainXml.followingCountText.setText(String.valueOf(data.following));
        mainXml.bioText.setText(data.bio);
        Glide.with(getApplicationContext()).load(data.profilepic).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profileImg);
if(data.userid.equals(PreferenceUtil.getUserId())){
    ViewGroup.LayoutParams layoutParams=mainXml.shareProfileBtn.getLayoutParams();
    layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
    mainXml.shareProfileBtn.setLayoutParams(layoutParams);
    mainXml.followBtn.setVisibility(ImageView.GONE);
    mainXml.unfollowBtn.setVisibility(ImageView.GONE);
    mainXml.shareProfileBtn.setVisibility(ImageView.VISIBLE);
    mainXml.followsMsgText.setVisibility(TextView.GONE);
    mainXml.NotFollowsMsgText.setVisibility(TextView.GONE);
    // get user posts
    getPosts(intentData.getStringExtra("userid"));


}else{
    if(data.isFollowedByMe){
        if(data.isFollowRequestApproved()){mainXml.unfollowBtn.setVisibility(View.VISIBLE);
            // get user posts

            getPosts(intentData.getStringExtra("userid"));
        }
        else{
            mainXml.requestCancelBtn.setVisibility(View.VISIBLE);
            ifPrivateSetup(data);
        }

    }else{
        mainXml.followBtn.setVisibility(View.VISIBLE);
       ifPrivateSetup(data);

    }
    if(data.isFollowingMe){
        mainXml.followsMsgText.setVisibility(View.VISIBLE);
        mainXml.NotFollowsMsgText.setVisibility(View.GONE);

    }else{
        mainXml.followsMsgText.setVisibility(View.GONE);
        mainXml.NotFollowsMsgText.setVisibility(View.VISIBLE);
    }




    mainXml.followBtn.setOnClickListener(v -> {
        mainXml.followBtn.setVisibility(View.GONE);

        if(data.isPrivate()){
            mainXml.requestCancelBtn.setVisibility(View.VISIBLE);
        }else{
            mainXml.unfollowBtn.setVisibility(View.VISIBLE);
        }


        followManager.follow(data.userid, new NetworkCallbackInterfaceJsonObject() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONObject data=response.optJSONObject("data");
                assert data != null;
                String status=data.optString("status");
                if(status.equals(FollowRouteResponse.SUCCESS.toString())){
                    return;
                }
                mainXml.requestCancelBtn.setVisibility(View.VISIBLE);
                mainXml.followBtn.setEnabled(true);


            }

            @Override
            public void onError(int errorCode) {
                mainXml.followBtn.setVisibility(View.VISIBLE);

                if(data.isPrivate()){
                    mainXml.requestCancelBtn.setVisibility(View.GONE);
                }else{
                    mainXml.unfollowBtn.setVisibility(View.GONE);
                }
                mainXml.followBtn.setEnabled(true);

            }
        });
    });
    mainXml.unfollowBtn.setOnClickListener(v -> {
        mainXml.unfollowBtn.setVisibility(View.GONE);
        mainXml.followBtn.setVisibility(View.VISIBLE);
        mainXml.unfollowBtn.setEnabled(false);
        followManager.unfollow(data.userid, new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {
                mainXml.unfollowBtn.setEnabled(true);
                getProfileData();

            }

            @Override
            public void onError(String err) {
                mainXml.unfollowBtn.setVisibility(View.VISIBLE);
                mainXml.followBtn.setVisibility(View.GONE);
                mainXml.unfollowBtn.setEnabled(true);

            }
        });

    });
    mainXml.requestCancelBtn.setOnClickListener(v->{
        mainXml.followBtn.setVisibility(View.VISIBLE);
        mainXml.requestCancelBtn.setEnabled(false);
        followManager.cancelFollowRequest(data.getUserid(), new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {
             mainXml.requestCancelBtn.setVisibility(View.GONE);
             mainXml.followBtn.setVisibility(View.VISIBLE);
             mainXml.requestCancelBtn.setEnabled(true);
            }

            @Override
            public void onError(String err) {
                mainXml.requestCancelBtn.setVisibility(View.VISIBLE);
                mainXml.followBtn.setVisibility(View.GONE);
                mainXml.requestCancelBtn.setEnabled(true);

            }
        });
    });



}



        

    }

    private void ifPrivateSetup(Profile_Model data){
        if(data.isPrivate()){
            mainXml.accountPrivateBanner.setVisibility(View.VISIBLE);
            mainXml.postsShimmer.setVisibility(View.GONE);
           mainXml.postsRecyclerView.setVisibility(View.GONE);
            mainXml.lockIcon.setVisibility(View.VISIBLE);
        }else {
            mainXml.accountPrivateBanner.setVisibility(View.GONE);
            mainXml.postsRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    private void getPosts(String userId){
page=1;
        isLoading=true;
        postsManager.getUserPosts(page,userId, new NetworkCallbackInterfaceJsonObject() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(JSONObject response) {
                mainXml.postsShimmer.setVisibility(View.GONE);
                mainXml.postsRecyclerView.setVisibility(View.VISIBLE);
                try {
                    postsArray.clear();
                    JSONArray array=response.getJSONArray("data");
                    if(array.length()==0){isLastPage=true;isLoading=false;return;}
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        postsArray.add(new Posts_Model(0,
                                object.getInt("postid"),
                                object.getString("userid"),
                                object.getString("username"),
                                object.getString("profilepic"),
                                object.getString("imageurl"),
                                object.getString("caption"),
                                object.getString("created_at"),
                                object.getString("likedBy"),
                                object.getInt("likeCount"),
                                object.getInt("commentCount"),
                                object.getInt("shareCount"),
                                object.getInt("isLiked")
                                ,object.getString("type").equals("video"),
                                object.getInt("isFollowed")>0
                        ));

                    }
                    isLoading=false;
                    postAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    isLoading=false;
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(int errorCode) {
                isLoading=false;
                Log.d("PostLoadingErr",Integer.toString(errorCode));

            }
        });

    }
    private void getMorePosts(int PageNo,String userId){
        isLoading=true;
        postsManager.getUserPosts(PageNo,userId, new NetworkCallbackInterfaceJsonObject() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(JSONObject response) {
                mainXml.postsShimmer.setVisibility(View.GONE);
                mainXml.postsRecyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONArray array=response.getJSONArray("data");
                    if(array.length()==0){isLastPage=true;
                        isLoading=false;
                        return;}
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        postsArray.add(new Posts_Model(0,
                                object.getInt("postid"),
                                object.getString("userid"),
                                object.getString("username"),
                                object.getString("profilepic"),
                                object.getString("imageurl"),
                                object.getString("caption"),
                                object.getString("created_at"),
                                object.getString("likedBy"),
                                object.getInt("likeCount"),
                                object.getInt("commentCount"),
                                object.getInt("shareCount"),
                                object.getInt("isLiked")
                                ,object.getString("type").equals("video"),
                                object.getInt("isFollowed")>0
                        ));

                    }
                    isLoading=false;
                    postAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    isLoading=false;
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(int errorCode) {
                isLoading=false;
                Log.d("PostLoadingErr",Integer.toString(errorCode));

            }
        });

    }
    private void loadMore(){
        if(!isLoading&&!isLastPage){
            isLoading=true;
            getMorePosts(++page,intentData.getStringExtra("userid"));
        }
    }


}