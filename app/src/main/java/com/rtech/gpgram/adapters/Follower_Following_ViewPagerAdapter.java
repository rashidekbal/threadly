package com.rtech.gpgram.adapters;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rtech.gpgram.fragments.follower_following_fragments.Followers;
import com.rtech.gpgram.fragments.follower_following_fragments.Followings;

public class Follower_Following_ViewPagerAdapter extends  FragmentPagerAdapter{
    private final String userId;


    public Follower_Following_ViewPagerAdapter(@NonNull FragmentManager fm,String userid) {
        super(fm);
        this.userId= userid;


    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("userid", userId);
        if(position==0){
            Followers followers = new Followers();
            followers.setArguments(bundle);
            return followers;
        } else{
           Followings followings = new Followings();
            followings.setArguments(bundle);
            return followings;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Followers";
        } else {
            return "Followings";
    }
}}
