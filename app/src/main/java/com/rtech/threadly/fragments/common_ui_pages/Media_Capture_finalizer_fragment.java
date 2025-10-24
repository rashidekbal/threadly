package com.rtech.threadly.fragments.common_ui_pages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rtech.threadly.databinding.FragmentImageFinalizerFragmentBinding;
import com.rtech.threadly.interfaces.general_ui_callbacks.OnCapturedMediaFinalizedCallback;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.io.File;
import java.util.Objects;

public class Media_Capture_finalizer_fragment extends Fragment {
    File imageFile;
    String mediaType;
    FragmentImageFinalizerFragmentBinding mainXMl;
    OnCapturedMediaFinalizedCallback oncapturedMediaFinalizedCallback;


    public Media_Capture_finalizer_fragment(

    ) {
        // Required empty public constructor
    }
    public Media_Capture_finalizer_fragment(OnCapturedMediaFinalizedCallback onCapturedMediaFinalized){
        this.oncapturedMediaFinalizedCallback=onCapturedMediaFinalized;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXMl=FragmentImageFinalizerFragmentBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        init();
        return mainXMl.getRoot();
    }

    private void init(){
        if(getArguments()!=null) {
            Bundle bundle = getArguments();
            mediaType=bundle.getString("mediaType");
            imageFile=new File(Objects.requireNonNull(bundle.getString("filePath")));
            //if image r video doesn't exist pop backStack
            if(!imageFile.exists()){
                requireActivity().getSupportFragmentManager().popBackStack();
                ReUsableFunctions.ShowToast("Image doesn't exists");
            }
            Glide.with(requireActivity()).load(imageFile).into(mainXMl.mediaPreviewView);
        }else{
            requireActivity().getSupportFragmentManager().popBackStack();
            ReUsableFunctions.ShowToast("no media captured");

        }
        // add buttons functionality

       mainXMl.discardBtn.setOnClickListener(v->{
           imageFile.delete();
           oncapturedMediaFinalizedCallback.discard();

       });
        mainXMl.ApproveBtn.setOnClickListener(v->{
          oncapturedMediaFinalizedCallback.OnFinalized(imageFile.getAbsolutePath(),mediaType);
        });







        }

}