package com.rtech.threadly.fragments.MessageFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentAudioSendMessageBinding;


public class AudioSendMessageFragment extends Fragment {
    FragmentAudioSendMessageBinding mainXml;

    public AudioSendMessageFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentAudioSendMessageBinding.inflate(inflater,container,false);

        return mainXml.getRoot();
    }
}