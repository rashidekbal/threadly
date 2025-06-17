package com.rtech.gpgram.fragments;

import static com.bumptech.glide.Priority.HIGH;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.R;
import com.rtech.gpgram.models.ProfileDataStructure;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class profileFragment extends Fragment {
    SharedPreferences loginInfo;
    TextView userid_text,username_text,posts_count_text,followers_count_text,following_count_text,bio_text;
    BottomNavigationView profile_bottom_navigation_view;
    ImageView profileImg;
    String baseUrl= BuildConfig.BASE_URL;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout profileLayout;
    // Base URL for the API, can be set in BuildConfig or directly here
    ProfileDataStructure userdata;
Post_fragmentSetCallback callback;

    public profileFragment(Post_fragmentSetCallback callback) {
        // Required empty public constructor
        this.callback=callback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View v=inflater.inflate(R.layout.fragment_profile, container, false);
     init(v);


        profile_bottom_navigation_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.posts){
                    change_fragment(new Posts_of_profile(new Post_fragmentSetCallback() {
                        @Override
                        public void openPostFragment(String url, int postid) {
                            callback.openPostFragment(url,postid);

                        }
                    }));

                }else if (item.getItemId()==R.id.tags){}                return true;
            }
        });
profile_bottom_navigation_view.setSelectedItemId(R.id.posts);
        return v;
    }


    public void init(View v){
        userid_text=v.findViewById(R.id.userid_text);
        username_text=v.findViewById(R.id.username_text);
        profile_bottom_navigation_view=v.findViewById(R.id.profile_bottom_navigation_view);
        profileImg=v.findViewById(R.id.profile_img);
        posts_count_text=v.findViewById(R.id.posts_count_text);
        followers_count_text=v.findViewById(R.id.followers_count_text);
        following_count_text=v.findViewById(R.id.following_count_text);
        bio_text=v.findViewById(R.id.bio_text);
        AndroidNetworking.initialize(v.getContext());
        shimmerFrameLayout=v.findViewById(R.id.profile_shimmer);

        profileLayout=v.findViewById(R.id.profile_layout);
        loginInfo=getContext().getSharedPreferences("loginInfo",v.getContext().MODE_PRIVATE);
        getProfileData(v);


    }
    private void getProfileData(View v) {
        String getProfileInfoUrl=baseUrl.concat("/users/getMyData");
        // Make network request to get profile data
        AndroidNetworking.get(getProfileInfoUrl)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(loginInfo.getString("token","null")))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener(){

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array= response.getJSONArray("data");
                            JSONObject object=array.getJSONObject(0);

                        ;

                                userdata=new ProfileDataStructure(
                                    object.getString("userid")
                                    ,object.getString("username")
                                    ,object.getString("profilepic")
                                    ,object.getString("bio")
                                    ,object.getString("dob").split("T")[0]
                                    ,object.getInt("followersCount")
                                    ,object.getInt("followingCount")
                                    ,object.getInt("PostsCount")
                                    ,0);
                                setUserdata(v);



                        } catch (JSONException e) {



                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("profileDataErr", anError.getMessage());

                    }
                });


    }

    public void change_fragment(Fragment fragment){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_profile_page,fragment)
                .commit();

    }

    private void setUserdata(View v){
        if(userdata.profilepic.equals("null")){
            Glide.with(v.getContext()).load(R.drawable.blank_profile).circleCrop().priority(Priority.HIGH).into(profileImg);}
        else {
            Glide.with(v.getContext()).load(userdata.profilepic).circleCrop().priority(HIGH).into(profileImg);
        }
        userid_text.setText(userdata.userid);
        username_text.setText(userdata.username);
        posts_count_text.setText(String.valueOf(userdata.posts));
        followers_count_text.setText(String.valueOf(userdata.followers));
        following_count_text.setText(String.valueOf(userdata.following));
        bio_text.setText(userdata.bio);
        shimmerFrameLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);


    }
}