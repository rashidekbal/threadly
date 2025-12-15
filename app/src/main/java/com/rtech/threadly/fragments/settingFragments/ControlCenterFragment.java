package com.rtech.threadly.fragments.settingFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.settings.PasswordChangeActivity;
import com.rtech.threadly.activities.settings.SettingsActivity;
import com.rtech.threadly.activities.settings.UserActivitiesPageActivity;
import com.rtech.threadly.databinding.FragmentControlCenterBinding;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.viewmodels.ProfileViewModel;


public class ControlCenterFragment extends Fragment {
    FragmentControlCenterBinding mainXml;
    private  ProfileViewModel profileViewModel;
    private Profile_Model  userData;


    public ControlCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentControlCenterBinding.inflate(inflater,container,false);
        init();
        setClickHandler();
        return mainXml.getRoot();
    }
    private void init (){
        profileViewModel=new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.getProfileLiveData().observe(requireActivity(),profileModel -> {
            userData=profileModel;
        });
    }
    private  void setClickHandler(){
        mainXml.backBtn.setOnClickListener(v->requireActivity().getSupportFragmentManager().popBackStack());
        mainXml.yourActivities.setOnClickListener(v->startActivity(new Intent(requireActivity(), UserActivitiesPageActivity.class)));
        mainXml.passwordSecurity.setOnClickListener(v->startActivity(new Intent(requireActivity(), PasswordChangeActivity.class)));
    }
}