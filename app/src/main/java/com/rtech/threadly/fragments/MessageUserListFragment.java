package com.rtech.threadly.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.databinding.FragmentMessageUserListBinding;


public class MessageUserListFragment extends Fragment {
FragmentMessageUserListBinding mainXml;


    public MessageUserListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     mainXml=FragmentMessageUserListBinding.inflate(inflater,container,false);
        return mainXml.getRoot();
    }
}