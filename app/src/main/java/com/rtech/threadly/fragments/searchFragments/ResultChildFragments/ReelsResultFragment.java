package com.rtech.threadly.fragments.searchFragments.ResultChildFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentReelsResultBinding;


public class ReelsResultFragment extends Fragment {
FragmentReelsResultBinding mainXml;

    public ReelsResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentReelsResultBinding.inflate(inflater,container,false);
        return mainXml.getRoot();
    }
}