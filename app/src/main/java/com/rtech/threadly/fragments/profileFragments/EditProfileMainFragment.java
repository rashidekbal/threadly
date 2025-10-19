package com.rtech.threadly.fragments.profileFragments;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.databinding.FragmentEditProfileMainBinding;
import com.rtech.threadly.interfaces.FragmentItemClickInterface;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.viewmodels.ProfileViewModel;

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
        Glide.with(Threadly.getGlobalContext()).load(R.drawable.blank_profile).circleCrop().into(mainXml.profileImage);
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

       }
       // views

       ImageView profilePic=bottomSheetDialog.findViewById(R.id.profilePic);
       LinearLayout openCameraButton=bottomSheetDialog.findViewById(R.id.openCameraButton);
       LinearLayout openGallery=bottomSheetDialog.findViewById(R.id.pictureSelector_gallery_btn);
       Glide.with(Threadly.getGlobalContext()).load(userdata.profilepic).placeholder(R.drawable.blank_profile).error(R.drawable.blank_profile).circleCrop().into(profilePic);
      openCameraButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               callback.onItemClick(openCameraButton);
               bottomSheetDialog.cancel();

           }
       });
       openGallery.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               callback.onItemClick(openGallery);
               bottomSheetDialog.cancel();

           }
       });
       bottomSheetDialog.show();

   }



    @Override
    public void onDestroy() {
        super.onDestroy();
        callback.onFragmentDestroy();
    }



}