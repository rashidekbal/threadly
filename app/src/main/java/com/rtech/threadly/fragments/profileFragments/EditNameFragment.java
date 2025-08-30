package com.rtech.threadly.fragments.profileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentEditNameBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.ProfileEditorManager;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;


public class EditNameFragment extends Fragment {
    FragmentEditNameBinding mainXml;
    ProfileEditorManager profileEditorManager;
    Profile_Model userdata;
    ProfileViewModel profileViewModel;

    public EditNameFragment() {


        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentEditNameBinding.inflate(inflater,container,false);
        AppCompatActivity activity=(AppCompatActivity) requireActivity();
        activity.setSupportActionBar(mainXml.toolbar);
        profileEditorManager=new ProfileEditorManager();
        profileViewModel=new ViewModelProvider(activity).get(ProfileViewModel.class);
        InputMethodManager imm=(InputMethodManager) activity.getSystemService(requireActivity().INPUT_METHOD_SERVICE);



        profileViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), new Observer<Profile_Model>() {
            @Override
            public void onChanged(Profile_Model profileModel) {
                userdata=profileModel;
                setUserdata(userdata);

            }
        });





        mainXml.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });


        mainXml.saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainXml.nameField.getWindowToken(), 0);
                mainXml.cancelButton.setEnabled(false);
                mainXml.saveButton.setEnabled(false);
                mainXml.warningText.setVisibility(View.GONE);

                String name=mainXml.nameField.getText().toString().trim();
                if(name.isEmpty()){
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                    mainXml.warningText.setVisibility(View.VISIBLE);
                    mainXml.warningText.setText(R.string.emptyUserName);


                }else if(name.length()<3||name.length()>20){
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                    mainXml.warningText.setVisibility(View.VISIBLE);
                    mainXml.warningText.setText(R.string.nameLengthError);

                }else{
                    mainXml.progressBar.setVisibility(View.VISIBLE);
                    profileEditorManager.UpdateName(name, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            mainXml.progressBar.setVisibility(View.GONE);
                            try {
                                profileViewModel.loadProfile();
                                JSONObject data=response.getJSONObject("data");
                                String name=data.getString("newName");
                                profileEditorManager.updatePreferences(name);
                                mainXml.cancelButton.setEnabled(true);
                                mainXml.saveButton.setEnabled(true);
                                ReUsableFunctions.ShowToast(activity,"Name Updated Successfully");
                                activity.onBackPressed();

                            } catch (JSONException e) {
                                mainXml.progressBar.setVisibility(View.GONE);
                                mainXml.cancelButton.setEnabled(true);
                                mainXml.saveButton.setEnabled(true);
                                mainXml.warningText.setVisibility(View.VISIBLE);
                                mainXml.warningText.setText(R.string.something_wentWrong);
                                if (BuildConfig.DEBUG) {
                                    e.printStackTrace();
                                }
                                throw new RuntimeException(e);
                            }
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

        return mainXml.getRoot();
    }


    private void setUserdata(Profile_Model userdata){
        if(userdata!=null){
            mainXml.nameField.setText(userdata.username);
        }

    }
}