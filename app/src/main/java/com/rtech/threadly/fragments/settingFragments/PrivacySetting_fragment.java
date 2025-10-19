package com.rtech.threadly.fragments.settingFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.rtech.threadly.databinding.FragmentPrivacySettingBinding;

public class PrivacySetting_fragment extends Fragment {
     FragmentPrivacySettingBinding mainXml;

    public PrivacySetting_fragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentPrivacySettingBinding.inflate(inflater,container,false);
        setUpFragment();
        return mainXml.getRoot();
    }



    private void setUpFragment(){
        mainXml.backBtn.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        mainXml.privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

    }


}