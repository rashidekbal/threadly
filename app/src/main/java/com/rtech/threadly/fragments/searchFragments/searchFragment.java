package com.rtech.threadly.fragments.searchFragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.constants.HomeActivityFragmentsIdEnum;
import com.rtech.threadly.databinding.FragmentSearchBinding;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ExplorePostsViewModel;

import java.util.ArrayList;

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

                changeFragment(new SearchReusltFragment(),HomeActivityFragmentsIdEnum.SEARCH_RESULT_FRAGMENT.toString());
            }
        });
    }

    private void setBackPressBehaviour() {
        requireActivity().getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    private void init() {
        //just loading for keeping data alive even when child fragment is killed or sent background or anything else
        changeFragment(new ExploreFragment(), HomeActivityFragmentsIdEnum.EXPLORE_FRAGMENT.toString());
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getCurrentEntryCount()==1){
                    mainXml.searchContainer.setVisibility(View.VISIBLE);
                    return ;
                }
                mainXml.searchContainer.setVisibility(View.GONE);
            }
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