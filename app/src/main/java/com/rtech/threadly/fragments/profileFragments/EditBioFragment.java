package com.rtech.threadly.fragments.profileFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentEditBioBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.ProfileEditorManager;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ProfileViewModel;


public class EditBioFragment extends Fragment {
    FragmentEditBioBinding mainXml;
    Profile_Model userdata;
    ProfileViewModel profileViewModel;
    ProfileEditorManager profileEditorManager;


    public EditBioFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentEditBioBinding.inflate(inflater,container,false);
        AppCompatActivity activity=(AppCompatActivity) requireActivity();
        activity.setSupportActionBar(mainXml.toolbar);
        profileEditorManager=new ProfileEditorManager();
        profileViewModel=new ViewModelProvider(activity).get(ProfileViewModel.class);
        profileViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), new Observer<Profile_Model>() {
            @Override
            public void onChanged(Profile_Model profileModel) {
                userdata=profileModel;
                if(userdata!=null){
                    mainXml.bioEdittext.setText(userdata.bio);
                }
            }
        });
        mainXml.bioEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mainXml.characterCount.setText(s.length()+"/150");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mainXml.characterCount.setText(s.length()+"/150");

            }

            @Override
            public void afterTextChanged(Editable s) {
                mainXml.characterCount.setText(s.length()+"/150");

            }
        });
        mainXml.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainXml.bioEdittext.getWindowToken(),0);
                mainXml.cancelButton.setEnabled(false);
                mainXml.saveButton.setEnabled(false);
                mainXml.warningText.setVisibility(View.INVISIBLE);
                String bio=mainXml.bioEdittext.getText().toString().trim();
                if(bio.isEmpty()){
                    mainXml.warningText.setVisibility(View.VISIBLE);
                    mainXml.warningText.setText(R.string.bioEmptyWarning);
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                }else if(bio.length()>150){
                    mainXml.warningText.setText(R.string.BioLengthWarning);
                    mainXml.warningText.setVisibility(View.VISIBLE);
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                }else{
                    mainXml.progressBar.setVisibility(View.VISIBLE);

                    profileEditorManager.UpdateUserBio(bio, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.cancelButton.setEnabled(true);
                            mainXml.saveButton.setEnabled(true);
                            profileViewModel.loadProfile();
                            ReUsableFunctions.ShowToast(activity,"Bio Updated Successfully");
                            activity.onBackPressed();
                        }

                        @Override
                        public void onError(String err) {
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.cancelButton.setEnabled(true);
                            mainXml.saveButton.setEnabled(true);
                            mainXml.warningText.setVisibility(View.VISIBLE);
                            mainXml.warningText.setText(R.string.something_wentWrong);

                        }
                    });



                }



            }
        });


        mainXml.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        return mainXml.getRoot();
    }
}