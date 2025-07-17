package com.rtech.threadly.fragments.profileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentUsernameEditBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.managers.ProfileEditorManager;
import com.rtech.threadly.models.Profile_Model;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class UsernameEditFragment extends Fragment {
    FragmentUsernameEditBinding mainXml;
    ProfileEditorManager profileEditorManager;
    ProfileViewModel profileViewModel;
    Profile_Model userdata;


    public UsernameEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentUsernameEditBinding.inflate(inflater,container,false);
        AppCompatActivity activity=(AppCompatActivity) requireActivity();
        activity.setSupportActionBar(mainXml.toolbar);
        profileEditorManager=new ProfileEditorManager();
        profileViewModel=new ViewModelProvider(activity).get(ProfileViewModel.class);

        profileViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), new Observer<Profile_Model>() {
            @Override
            public void onChanged(Profile_Model profileModel) {
                userdata=profileModel;
                if(userdata!=null){
                    mainXml.useridField.setText(userdata.userid);
                }

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
                InputMethodManager imm=(InputMethodManager) activity.getSystemService(requireActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainXml.useridField.getWindowToken(),0);
                mainXml.cancelButton.setEnabled(false);
                mainXml.saveButton.setEnabled(false);
                String userid=mainXml.useridField.getText().toString().trim();
                mainXml.errorMessageText.setVisibility(View.GONE);
                if(userid.isEmpty()){
                    mainXml.errorMessageText.setVisibility(View.VISIBLE);
                    mainXml.errorMessageText.setText(R.string.emptyUserNameError);
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                    return;
                } else if (userid.length()<6) {
                    mainXml.errorMessageText.setVisibility(View.VISIBLE);
                    mainXml.errorMessageText.setText(R.string.usernameLengthError);
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                    return;
                } else if (userid.length()>20) {
                    mainXml.errorMessageText.setVisibility(View.VISIBLE);
                    mainXml.errorMessageText.setText(R.string.usernameTooLongError);
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                    return;
                } else if (!userid.matches("[a-zA-Z0-9_]+")) {
                    mainXml.errorMessageText.setVisibility(View.VISIBLE);
                    mainXml.errorMessageText.setText(R.string.usernameContaminationError);
                    mainXml.cancelButton.setEnabled(true);
                    mainXml.saveButton.setEnabled(true);
                    return;
                    
                }else{
                    mainXml.progressBar.setVisibility(View.VISIBLE);
                    profileEditorManager.UpdateUserid(userid, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            mainXml.saveButton.setEnabled(true);
                            try {
                                JSONObject data=response.getJSONObject("data");
                                String token=data.getString("token");
                                profileEditorManager.updatePreferences(token,userid);
                                mainXml.progressBar.setVisibility(View.GONE);
                                mainXml.cancelButton.setEnabled(true);
                                profileViewModel.loadProfile();
                                ReUsableFunctions.ShowToast(activity,"Username Updated Successfully");
                                activity.onBackPressed();

                            } catch (JSONException e) {
                                mainXml.errorMessageText.setVisibility(View.VISIBLE);
                                mainXml.saveButton.setEnabled(true);
                                mainXml.progressBar.setVisibility(View.GONE);
                                mainXml.cancelButton.setEnabled(true);
                                mainXml.errorMessageText.setText(R.string.something_wentWrong);
                                throw new RuntimeException(e);
                            }

                        }

                        @Override
                        public void onError(String err) {
                            int ErrorCode=Integer.parseInt(err);
                            mainXml.errorMessageText.setVisibility(View.VISIBLE);
                            mainXml.saveButton.setEnabled(true);
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.cancelButton.setEnabled(true);
                            if(ErrorCode==409){

                                mainXml.errorMessageText.setText(R.string.usernameExists);

                            }else{
                                mainXml.errorMessageText.setText(R.string.something_wentWrong);

                            }

                        }
                    });

                }
            }
        });

        return mainXml.getRoot();
    }

}