package com.rtech.threadly.fragments.profileFragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rtech.threadly.databinding.FragmentProfileUploadFinalPreviewBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.ProfileEditorManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class profileUploadFinalPreview extends Fragment {
    FragmentProfileUploadFinalPreviewBinding mainXml;
    AppCompatActivity activity;
    String ImagePath;
    ProfileViewModel profileViewModel;
  ProfileEditorManager profileEditorManager;


    public profileUploadFinalPreview() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentProfileUploadFinalPreviewBinding.inflate(inflater,container,false);
        activity=(AppCompatActivity) requireActivity();
        activity.setSupportActionBar(mainXml.toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        profileEditorManager=new ProfileEditorManager();
        ImagePath=getArguments().getString("path");
        profileViewModel=new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        Glide.with(requireContext()).load(new File(ImagePath)).into(mainXml.previewView);
        SetOnclickListeners();



        return mainXml.getRoot();
    }

    private void SetOnclickListeners() {
        mainXml.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        mainXml.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainXml.uploadBtn.setEnabled(false);
                mainXml.progressBar.setVisibility(View.VISIBLE);
                profileEditorManager.ChangeUserProfile(new File(ImagePath), new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONObject data=response.getJSONObject("data");
                            String url=data.getString("url");
                            profileEditorManager.updateUserProfile(url);
                            profileViewModel.loadProfile();
                            new File(ImagePath).delete();
                            activity.onBackPressed();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }

                    @Override
                    public void onError(String err) {
                        mainXml.uploadBtn.setEnabled(true);
                        mainXml.progressBar.setVisibility(View.GONE);
                        ReUsableFunctions.ShowToast("upload failed");
                        activity.onBackPressed();


                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        if(new File(ImagePath).exists()){
            new File(ImagePath).delete();
        }
        super.onDestroy();
    }
}