package com.rtech.gpgram.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.GridPostAdapter;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;
import com.rtech.gpgram.managers.FollowManager;
import com.rtech.gpgram.managers.PostsManager;
import com.rtech.gpgram.managers.ProfileManager;
import com.rtech.gpgram.models.Preview_Post_model;
import com.rtech.gpgram.models.Profile_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    ProfileManager profileManager;
    Intent intentData;
    Profile_Model profileData;
    TextView userid_text,posts_count_text,followers_count_text,following_count_text,username_text,bio_text,follows_msg_Text,Not_follows_msg_Text;
    ImageView profile_img;
    AppCompatButton unfollow_btn,follow_btn,shareProfile_btn;
    ShimmerFrameLayout posts_shimmer,profile_shimmer;
    RecyclerView posts_recyclerView;
    GridPostAdapter postAdapter;
    ArrayList<Preview_Post_model> postsArray=new ArrayList<>();
    StaggeredGridLayoutManager layoutManager;
    PostsManager postsManager;
    FollowManager followManager;
    LinearLayout mainProfileLayout;
    SharedPreferences loginInfo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
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

        //on click listeners for followers and following text views
        followers_count_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfileActivity.this, FollowerFollowingList.class);
                intent.putExtra("type","followers");
                intent.putExtra("userid",intentData.getStringExtra("userid"));
                startActivity(intent);
            }
        });
        following_count_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfileActivity.this, FollowerFollowingList.class);
                intent.putExtra("type","following");
                intent.putExtra("userid",intentData.getStringExtra("userid"));
                startActivity(intent);
            }
        });





    }

    private void init(){
        profileManager=new ProfileManager(UserProfileActivity.this);
        postsManager=new PostsManager(UserProfileActivity.this);
        followManager=new FollowManager(UserProfileActivity.this);
        intentData=getIntent();
        userid_text=findViewById(R.id.userid_text);
        posts_count_text=findViewById(R.id.posts_count_text);
        followers_count_text=findViewById(R.id.followers_count_text);
        following_count_text=findViewById(R.id.following_count_text);
        follows_msg_Text=findViewById(R.id.follows_msg_Text);
        Not_follows_msg_Text=findViewById(R.id.Not_follows_msg_Text);
        username_text=findViewById(R.id.username_text);
        bio_text=findViewById(R.id.bio_text);
        profile_img=findViewById(R.id.profile_img);
        unfollow_btn=findViewById(R.id.unfollow_btn);
        follow_btn=findViewById(R.id.follow_btn);
        shareProfile_btn=findViewById(R.id.shareProfile_btn);
        posts_shimmer=findViewById(R.id.posts_shimmer);
        profile_shimmer=findViewById(R.id.profile_shimmer);
        posts_recyclerView=findViewById(R.id.posts_recyclerView);
        mainProfileLayout=findViewById(R.id.profile_layout);
        loginInfo=getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,MODE_PRIVATE);

        layoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        postAdapter=new GridPostAdapter(UserProfileActivity.this, postsArray, new Post_fragmentSetCallback() {
            @Override
            public void openPostFragment(String url, int postid) {
                Intent intent=new Intent(UserProfileActivity.this,PostActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("postid",postid);
                startActivity(intent);

            }
        });
        posts_recyclerView.setLayoutManager(layoutManager);
        posts_recyclerView.setAdapter(postAdapter);





    }
    private void getProfileData(){
        profileManager.GetProfile(intentData.getStringExtra("userid"), new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                profile_shimmer.setVisibility(ShimmerFrameLayout.GONE);
                mainProfileLayout.setVisibility(LinearLayout.VISIBLE);
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
        userid_text.setText(data.userid);
        username_text.setText(data.username);
        posts_count_text.setText(String.valueOf(data.posts));
        followers_count_text.setText(String.valueOf(data.followers));
        following_count_text.setText(String.valueOf(data.following));
        bio_text.setText(data.bio);
        Glide.with(getApplicationContext()).load(data.profilepic).placeholder(R.drawable.blank_profile).circleCrop().into(profile_img);
if(data.userid.equals(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"))){
    ViewGroup.LayoutParams layoutParams=shareProfile_btn.getLayoutParams();
    layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
    shareProfile_btn.setLayoutParams(layoutParams);
    follow_btn.setVisibility(ImageView.GONE);
    unfollow_btn.setVisibility(ImageView.GONE);
    shareProfile_btn.setVisibility(ImageView.VISIBLE);
    follows_msg_Text.setVisibility(TextView.GONE);
    Not_follows_msg_Text.setVisibility(TextView.GONE);


}else{
    if(data.isFollowedByMe){
        unfollow_btn.setVisibility(View.VISIBLE);
        follow_btn.setVisibility(View.GONE);
    }else{
        unfollow_btn.setVisibility(View.GONE);
        follow_btn.setVisibility(View.VISIBLE);
    }
    if(data.isFollowingMe){
        follows_msg_Text.setVisibility(View.VISIBLE);
        Not_follows_msg_Text.setVisibility(View.GONE);

    }else{
        follows_msg_Text.setVisibility(View.GONE);
        Not_follows_msg_Text.setVisibility(View.VISIBLE);
    }


    follow_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            unfollow_btn.setVisibility(View.VISIBLE);
            follow_btn.setVisibility(View.GONE);
            follow_btn.setEnabled(false);
            unfollow_btn.setEnabled(false);

            followManager.follow(data.userid, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    unfollow_btn.setEnabled(true);

                }

                @Override
                public void onError(String err) {
                    follow_btn.setVisibility(View.VISIBLE);
                    unfollow_btn.setVisibility(View.GONE);
                    follow_btn.setEnabled(true);
                    unfollow_btn.setEnabled(false);

                }
            });
        }
    });
    unfollow_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            unfollow_btn.setVisibility(View.GONE);
            follow_btn.setVisibility(View.VISIBLE);
            follow_btn.setEnabled(false);
            unfollow_btn.setEnabled(false);
            followManager.unfollow(data.userid, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    follow_btn.setEnabled(true);
                }

                @Override
                public void onError(String err) {
                    unfollow_btn.setVisibility(View.VISIBLE);
                    follow_btn.setVisibility(View.GONE);
                    follow_btn.setEnabled(false);
                    unfollow_btn.setEnabled(true);

                }
            });

        }
    });



}



        

    }
    private void getPosts(String userId){
        postsManager.getUserPosts(userId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                posts_shimmer.setVisibility(View.GONE);
                posts_recyclerView.setVisibility(View.VISIBLE);
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