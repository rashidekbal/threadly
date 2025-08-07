package com.rtech.threadly.fragments.settingFragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentSettingMainFragmentsBinding;
import com.rtech.threadly.utils.ReUsableFunctions;


public class setting_main_fragments extends Fragment {
FragmentSettingMainFragmentsBinding mainXml;

    public setting_main_fragments() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainXml=FragmentSettingMainFragmentsBinding.inflate(inflater,container,false);
        setOnclickListeners();
        return mainXml.getRoot();
    }

    private void setOnclickListeners() {
        mainXml.logoutBtn.setOnClickListener(v->{
            ReUsableFunctions.logout((AppCompatActivity) requireActivity());
            ReUsableFunctions.ShowToast("logged out");
        });
    }
}