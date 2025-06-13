package com.rtech.gpgram.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.gpgram.R;


public class profileFragment extends Fragment {
    SharedPreferences loginInfo;
    TextView userid_text,username_text;
    BottomNavigationView profile_bottom_navigation_view;
    ImageView profileImg;


    public profileFragment() {
        // Required empty public constructor
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
     loginInfo=getContext().getSharedPreferences("loginInfo",v.getContext().MODE_PRIVATE);
     userid_text.setText(loginInfo.getString("userid",""));
        username_text.setText(loginInfo.getString("username",""));
        Glide.with(v.getContext()).load(R.drawable.image_test).circleCrop().into(profileImg);

        profile_bottom_navigation_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.posts){
                    change_fragment(new Posts_of_profile());

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

    }
    public void change_fragment(Fragment fragment){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_profile_page,fragment)
                .commit();

    }
}