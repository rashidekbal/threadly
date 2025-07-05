package com.rtech.gpgram.fragments;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.gpgram.R;
import com.rtech.gpgram.databinding.FragmentEditProfileMainBinding;
import com.rtech.gpgram.databinding.ProfileUploadOptionsLayoutBinding;
import com.rtech.gpgram.interfaces.FragmentItemClickInterface;
import com.rtech.gpgram.models.Profile_Model;
import com.rtech.gpgram.viewmodels.ProfileViewModel;

public class EditProfileMainFragment extends Fragment {
FragmentItemClickInterface callback;
ProfileViewModel profileViewModel;
    AppCompatActivity activity;
    Profile_Model userdata;
    FragmentEditProfileMainBinding mainXml;
    public EditProfileMainFragment(FragmentItemClickInterface callback) {
        // Required empty public constructor
        this.callback = callback;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mainXml=FragmentEditProfileMainBinding.inflate(inflater,container,false);
        activity=(AppCompatActivity) requireActivity();
        activity.setSupportActionBar(mainXml.toolbar);
        activity.getSupportActionBar().setTitle("Edit profile");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        profileViewModel=new ViewModelProvider(activity).get(ProfileViewModel.class);


        profileViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), new Observer<Profile_Model>() {
            @Override
            public void onChanged(Profile_Model profileModel) {
                userdata=profileModel;
                setUserdata(userdata);
            }
        });

        mainXml.changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadOptions();

            }
        });


        mainXml.nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(v);

            }
        });
        mainXml.usernameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(v);

            }
        });
        mainXml.bioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(v);
            }
        });


        return mainXml.getRoot();
    }
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.onBackPressed();
            return true;
        }
        return false;
    }
    private  void setUserdata(Profile_Model userdata){

        if(userdata!=null){
            mainXml.nameField.setText(userdata.username);
            mainXml.useridField.setText(userdata.userid);
            mainXml.bioField.setText(userdata.bio);
            Glide.with(activity).load(userdata.profilepic).placeholder(R.drawable.blank_profile).error(R.drawable.blank_profile)
                    .circleCrop().into(mainXml.profileImage);

        }

   }

   private void showUploadOptions(){
       BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(activity,R.style.TransparentBottomSheet);
       FrameLayout frameLayout=bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
       bottomSheetDialog.setContentView(R.layout.profile_upload_options_layout);
       bottomSheetDialog.setCancelable(true);
       bottomSheetDialog.setCanceledOnTouchOutside(true);
       if(frameLayout!=null){
           BottomSheetBehavior<FrameLayout> behavior=BottomSheetBehavior.from(frameLayout);
           behavior.setDraggable(true);
           behavior.setState(STATE_EXPANDED);
           behavior.setFitToContents(true);

       }
       
       ProfileUploadOptionsLayoutBinding dialogBinding=ProfileUploadOptionsLayoutBinding.inflate(activity.getLayoutInflater());
       bottomSheetDialog.show();

   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback.onfragmentDestroy();
    }
}