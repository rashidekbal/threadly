package com.rtech.threadly.fragments.settingFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentPasswordChangeBinding;


public class Password_change_fragment extends Fragment {
    FragmentPasswordChangeBinding mainXml;

    public Password_change_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentPasswordChangeBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        setClickHandlers();
        return mainXml.getRoot();
    }

    private void setClickHandlers() {
        mainXml.backBtn.setOnClickListener(v->requireActivity().onBackPressed());
    }
}