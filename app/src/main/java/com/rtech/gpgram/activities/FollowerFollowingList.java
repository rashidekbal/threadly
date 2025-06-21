package com.rtech.gpgram.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.Follower_Following_ViewPagerAdapter;
import com.rtech.gpgram.managers.FollowManager;

public class FollowerFollowingList extends AppCompatActivity {
    Intent intentData;
    String type,userid;
    TabLayout tabLayout;
    ViewPager viewPager;
    Follower_Following_ViewPagerAdapter ViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follower_following_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize your views and set up the activity here
        init();

        viewPager.setAdapter(ViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);
        if(type.equals("followers")){
           tabLayout.selectTab(tabLayout.getTabAt(0));
        }
        else {
            tabLayout.selectTab(tabLayout.getTabAt(1));
        }




    }
    private void init(){
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        intentData=getIntent();
        userid=intentData.getStringExtra("userid");
        type=intentData.getStringExtra("type");
        ViewPagerAdapter=new Follower_Following_ViewPagerAdapter(getSupportFragmentManager(),userid);


    }
}