package com.rtech.threadly.fragments.settingFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.databinding.FragmentPasswordChangeBinding;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.network_managers.AuthManager;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONException;
import org.json.JSONObject;


public class Password_change_fragment extends Fragment {
    FragmentPasswordChangeBinding mainXml;
    AuthManager authManager;
    public Password_change_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentPasswordChangeBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        init();
        setClickHandlers();
        return mainXml.getRoot();
    }

    private void init() {
        authManager=new AuthManager();
    }

    private void setClickHandlers() {
        mainXml.backBtn.setOnClickListener(v->requireActivity().onBackPressed());
        mainXml.resetPasswordBtn.setOnClickListener(v->handlePasswordChange());
    }
    private void handlePasswordChange(){
        mainXml.progressBar.setVisibility(View.VISIBLE);
        String oldPassword=mainXml.oldPasswordField.getText().toString().trim();
        String newPassword1=mainXml.newPassword1Field.getText().toString().trim();
        String newPassword2=mainXml.newPassword2Field.getText().toString().trim();
        boolean isStateOk=validateInputs(oldPassword,newPassword1,newPassword2);
        //TODO: add regex for password validation
        if(!isStateOk){
            mainXml.progressBar.setVisibility(View.GONE);
            return;
        }
        mainXml.errorMessage.setVisibility(View.GONE);
        mainXml.progressBar.setVisibility(View.VISIBLE);
        handlePasswordChangeRequestToServer(oldPassword,newPassword1);

    }

    private boolean validateInputs(String oldPassword, String newPassword1, String newPassword2) {
    //TODO:ADD PROPER VALIDATION MESSAGE
        if(oldPassword.isEmpty()) {
            mainXml.oldPasswordField.setError("password field cannot be empty");
            return false ;
        }
        if(newPassword1.isEmpty()||newPassword1.length()<6){
            mainXml.newPassword1Field.setError("password must be at least 6 chars long");
            return false;}
        if(newPassword2.isEmpty()||newPassword2.length()<6){
            mainXml.newPassword2Field.setError("password must be same as above");
            return false;
        }
        if(!newPassword1.equals(newPassword2)){
            mainXml.errorMessage.setText("new password fields not matching");
            return false;
        }
        return true;
    }

    private void handlePasswordChangeRequestToServer(String oldPassword,String newPassword) {
        try {
            authManager.ResetPassword(oldPassword, newPassword, new NetworkCallbackInterfaceJsonObject() {
                @Override
                public void onSuccess(JSONObject response) {
                    mainXml.progressBar.setVisibility(View.GONE);
                    ReUsableFunctions.ShowToast("password changed successfully");
                    mainXml.oldPasswordField.setText("");
                    mainXml.newPassword2Field.setText("");
                    mainXml.newPassword1Field.setText("");

                }

                @Override
                public void onError(int errorCode) {
                    //TODO : handle error codes
                    ReUsableFunctions.ShowToast(" "+errorCode);
                    mainXml.progressBar.setVisibility(View.GONE);
                    mainXml.errorMessage.setText("something went wrong");
                    mainXml.errorMessage.setVisibility(View.VISIBLE);


                }
            });
        } catch (JSONException e) {
            mainXml.progressBar.setVisibility(View.GONE);
            mainXml.errorMessage.setText("something went wrong");
            mainXml.errorMessage.setVisibility(View.VISIBLE);

        }


    }
}