package com.rtech.threadly.fragments.common_ui_pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.databinding.FragmentImageFinalizerFragmentBinding;
import com.rtech.threadly.interfaces.general_ui_callbacks.OnCapturedMediaFinalizedCallback;

import java.io.File;

public class Image_finalizer_fragment extends Fragment {
    File imageFile;
    String mediaType;
    FragmentImageFinalizerFragmentBinding mainXMl;
    OnCapturedMediaFinalizedCallback OncapturedMediaFinalizedCallback;


    public Image_finalizer_fragment(

    ) {
        // Required empty public constructor
    }
    public Image_finalizer_fragment(OnCapturedMediaFinalizedCallback onCapturedMediaFinalized){
        this.OncapturedMediaFinalizedCallback=onCapturedMediaFinalized;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXMl=FragmentImageFinalizerFragmentBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        return mainXMl.getRoot();
    }
}