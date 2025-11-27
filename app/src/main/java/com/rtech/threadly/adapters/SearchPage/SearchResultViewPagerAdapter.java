package com.rtech.threadly.adapters.SearchPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rtech.threadly.fragments.searchFragments.ResultChildFragments.AccountsFragment;
import com.rtech.threadly.fragments.searchFragments.ResultChildFragments.ReelsResultFragment;

public class SearchResultViewPagerAdapter extends FragmentPagerAdapter {


    public SearchResultViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new AccountsFragment();
        }else {
            return new ReelsResultFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)return "Accounts";
        return "Posts";
    }
}
