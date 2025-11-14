package com.rtech.threadly.fragments.settingFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.rtech.threadly.databinding.FragmentPrivacySettingBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.network_managers.PrivacyManager;
import com.rtech.threadly.utils.PreferenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ProfileViewModel;

public class PrivacySetting_fragment extends Fragment {
     FragmentPrivacySettingBinding mainXml;
     ProfileViewModel profileViewModel;
    public PrivacySetting_fragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentPrivacySettingBinding.inflate(inflater,container,false);
        profileViewModel=new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        setUpFragment();
        return mainXml.getRoot();
    }



    private void setUpFragment(){
        profileViewModel.getProfileLiveData().observe((LifecycleOwner) requireActivity(), new Observer<Profile_Model>() {
            @Override
            public void onChanged(Profile_Model profileModel) {
                if(profileModel==null) {
                    ReUsableFunctions.ShowToast("Something went wrong");
                    requireActivity().getSupportFragmentManager().popBackStackImmediate();
                    return;
                }
                if(profileModel.isPrivate()){
                    mainXml.privacySwitch.setChecked(true);
                    return;
                }
                mainXml.privacySwitch.setChecked(false);
                mainXml.doneBtn.setVisibility(View.GONE);
                mainXml.doneBtn.setEnabled(false);
            }
        });
        mainXml.backBtn.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

       mainXml.privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               mainXml.doneBtn.setVisibility(View.VISIBLE);
               mainXml.doneBtn.setEnabled(true);
           }
       });

       mainXml.doneBtn.setOnClickListener(v->{
           mainXml.progressBar.setVisibility(View.VISIBLE);
           mainXml.doneBtn.setEnabled(false);
           if(mainXml.privacySwitch.isChecked()){

               //make account private
               PrivacyManager.setPrivate(new NetworkCallbackInterface() {
                   @Override
                   public void onSuccess() {
                       onSuccessCleanUp();
                       PreferenceUtil.setPrivate(true);

                   }

                   @Override
                   public void onError(String err) {
                       onErrorCleanUp();
                       mainXml.privacySwitch.setChecked(false);

                   }
               });
               return;
           }
           //set public
           PrivacyManager.setPublic(new NetworkCallbackInterface() {
               @Override
               public void onSuccess() {
                   onSuccessCleanUp();
                   PreferenceUtil.setPrivate(false);

               }

               @Override
               public void onError(String err) {
                   onErrorCleanUp();
                   mainXml.privacySwitch.setChecked(true);


               }
           });
       });

    }
    private void onErrorCleanUp(){
        mainXml.progressBar.setVisibility(View.GONE);
        mainXml.doneBtn.setEnabled(true);
        mainXml.doneBtn.setVisibility(View.GONE);
        ReUsableFunctions.ShowToast("Something went wrong");

    }
    private void onSuccessCleanUp(){
        profileViewModel.loadProfile();
        mainXml.progressBar.setVisibility(View.GONE);
        mainXml.doneBtn.setVisibility(View.GONE);
        mainXml.doneBtn.setEnabled(true);


    }

}