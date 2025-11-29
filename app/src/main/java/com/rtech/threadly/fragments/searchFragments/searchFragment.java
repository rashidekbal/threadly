package com.rtech.threadly.fragments.searchFragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.datatransport.runtime.dagger.Reusable;
import com.rtech.threadly.constants.HomeActivityFragmentsIdEnum;
import com.rtech.threadly.databinding.FragmentSearchBinding;
import com.rtech.threadly.utils.ReUsableFunctions;

public class searchFragment extends Fragment {
FragmentSearchBinding mainXml;
    public searchFragment() {
        // Required empty public constructor
    }
    OnBackPressedCallback backPressedCallback=new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if(getCurrentEntryCount()==0){
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
                return ;
            }
            if(getCurrentEntryCount()==1){
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
                return ;
            }

            getChildFragmentManager().popBackStack();


        }


    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentSearchBinding.inflate(inflater,container,false);
        setBackPressBehaviour();
        init();
        setSearchActionHandler();
        return mainXml.getRoot();
    }

    private void setSearchActionHandler() {
        mainXml.searchEditText.setOnFocusChangeListener((v,focus)->{
            if(focus){
                changeFragment(new SearchResultFragment(),HomeActivityFragmentsIdEnum.SEARCH_RESULT_FRAGMENT.toString());
            }
        });
    }

    private void setBackPressBehaviour() {
        requireActivity().getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    private void init() {
        //just loading for keeping data alive even when child fragment is killed or sent background or anything else
        int currentChildFragments=getChildFragmentManager().getBackStackEntryCount();
        if(currentChildFragments==0){
            changeFragment(new ExploreFragment(), HomeActivityFragmentsIdEnum.EXPLORE_FRAGMENT.toString());
        }
        getChildFragmentManager().addOnBackStackChangedListener(() -> {
            if(getChildFragmentManager().getBackStackEntryCount()==1){
                mainXml.searchContainer.setVisibility(View.VISIBLE);
                return ;
            }
            mainXml.searchContainer.setVisibility(View.GONE);
        });
    }
    private void changeFragment(Fragment fragment,String fragmentId){
        FragmentTransaction transaction= getChildFragmentManager().beginTransaction();
        transaction.replace(mainXml.fragmentContainer.getId(),fragment)
                .addToBackStack(fragmentId).commit();
    }
    private int getCurrentEntryCount() {
         return getChildFragmentManager().getBackStackEntryCount();
    }



}