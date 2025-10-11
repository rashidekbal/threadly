package com.rtech.threadly.activities;

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
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityUserProfileBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.network_managers.ProfileManager;
import com.rtech.threadly.models.Preview_Post_model;
import com.rtech.threadly.models.Profile_Model;

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
    ArrayList<Preview_Post_model> postsArray=new ArrayList<>();
    StaggeredGridLayoutManager layoutManager;
    PostsManager postsManager;
    FollowManager followManager;
    SharedPreferences loginInfo;




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
        // get user posts
        getPosts(intentData.getStringExtra("userid"));
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





    }

    private void init(){
        profileManager=new ProfileManager();
        postsManager=new PostsManager();
        followManager=new FollowManager();
        intentData=getIntent();

        loginInfo= Core.getPreference();

        layoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        postAdapter=new GridPostAdapter(UserProfileActivity.this, postsArray, new Post_fragmentSetCallback() {
            @Override
            public void openPostFragment(String url, int postid) {
                Intent intent=new Intent(UserProfileActivity.this,PostActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("postid",postid);
                startActivity(intent);

            }

            @Override
            public void openEditor() {

            }
        });
        mainXml.postsRecyclerView.setLayoutManager(layoutManager);
        mainXml.postsRecyclerView.setAdapter(postAdapter);





    }
    private void getProfileData(){
        profileManager.GetProfile(intentData.getStringExtra("userid"), new NetworkCallbackInterfaceWithJsonObjectDelivery() {
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
                            object.getInt("isFollowingUser"));

                    setData(profileData);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }



            }

            @Override
            public void onError(String err) {
                Log.d("error_loadingProfile",  err);

            }
        });

    }

    private void setData(Profile_Model data){
        mainXml.swipeRefresh.setRefreshing(false
        );
        mainXml.useridText.setText(data.userid);
        mainXml.usernameText.setText(data.username);
        mainXml.postsCountText.setText(String.valueOf(data.posts));
        mainXml.followersCountText.setText(String.valueOf(data.followers));
        mainXml.followingCountText.setText(String.valueOf(data.following));
        mainXml.bioText.setText(data.bio);
        Glide.with(getApplicationContext()).load(data.profilepic).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profileImg);
if(data.userid.equals(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"))){
    ViewGroup.LayoutParams layoutParams=mainXml.shareProfileBtn.getLayoutParams();
    layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
    mainXml.shareProfileBtn.setLayoutParams(layoutParams);
    mainXml.followBtn.setVisibility(ImageView.GONE);
    mainXml.unfollowBtn.setVisibility(ImageView.GONE);
    mainXml.shareProfileBtn.setVisibility(ImageView.VISIBLE);
    mainXml.followsMsgText.setVisibility(TextView.GONE);
    mainXml.NotFollowsMsgText.setVisibility(TextView.GONE);


}else{
    if(data.isFollowedByMe){
        mainXml.unfollowBtn.setVisibility(View.VISIBLE);
        mainXml.followBtn.setVisibility(View.GONE);
    }else{
        mainXml.unfollowBtn.setVisibility(View.GONE);
        mainXml.followBtn.setVisibility(View.VISIBLE);
    }
    if(data.isFollowingMe){
        mainXml.followsMsgText.setVisibility(View.VISIBLE);
        mainXml.NotFollowsMsgText.setVisibility(View.GONE);

    }else{
        mainXml.followsMsgText.setVisibility(View.GONE);
        mainXml.NotFollowsMsgText.setVisibility(View.VISIBLE);
    }


    mainXml.followBtn.setOnClickListener(v -> {
        mainXml.unfollowBtn.setVisibility(View.VISIBLE);
        mainXml.followBtn.setVisibility(View.GONE);
        mainXml.followBtn.setEnabled(false);
        mainXml.unfollowBtn.setEnabled(false);

        followManager.follow(data.userid, new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {
                mainXml.unfollowBtn.setEnabled(true);

            }

            @Override
            public void onError(String err) {
                mainXml.followBtn.setVisibility(View.VISIBLE);
                mainXml.unfollowBtn.setVisibility(View.GONE);
                mainXml.followBtn.setEnabled(true);
                mainXml.unfollowBtn.setEnabled(false);

            }
        });
    });
    mainXml.unfollowBtn.setOnClickListener(v -> {
        mainXml.unfollowBtn.setVisibility(View.GONE);
        mainXml.followBtn.setVisibility(View.VISIBLE);
        mainXml.followBtn.setEnabled(false);
        mainXml.unfollowBtn.setEnabled(false);
        followManager.unfollow(data.userid, new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {
                mainXml.followBtn.setEnabled(true);
            }

            @Override
            public void onError(String err) {
                mainXml.unfollowBtn.setVisibility(View.VISIBLE);
                mainXml.followBtn.setVisibility(View.GONE);
                mainXml.followBtn.setEnabled(false);
                mainXml.unfollowBtn.setEnabled(true);

            }
        });

    });



}



        

    }
    private void getPosts(String userId){
        postsManager.getUserPosts(userId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                mainXml.postsShimmer.setVisibility(View.GONE);
                mainXml.postsRecyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONArray array=response.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        postsArray.add(new Preview_Post_model(
                                object.getInt("postid"),
                                object.getString("imageurl")
                        ));

                    }
                    postAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                Log.d("PostLoadingErr", err);

            }
        });

    }


}