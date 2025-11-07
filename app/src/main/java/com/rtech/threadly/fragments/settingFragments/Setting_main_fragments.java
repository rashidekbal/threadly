package com.rtech.threadly.fragments.settingFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.activities.LoginActivity;
import com.rtech.threadly.databinding.FragmentSettingMainFragmentsBinding;
import com.rtech.threadly.interfaces.FragmentItemClickInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.utils.LoginSequenceUtil;
import com.rtech.threadly.utils.LogoutSequenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;


public class Setting_main_fragments extends Fragment {
FragmentSettingMainFragmentsBinding mainXml;
FragmentItemClickInterface clickInterface;


    public Setting_main_fragments() {
        // Required empty public constructor
    }
    public Setting_main_fragments(FragmentItemClickInterface clickInterface){
        this.clickInterface=clickInterface;

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainXml=FragmentSettingMainFragmentsBinding.inflate(inflater,container,false);
        setOnclickListeners();
        return mainXml.getRoot();
    }

    private void setOnclickListeners() {
        mainXml.logoutBtn.setOnClickListener(v->{
            mainXml.logoutBtn.setEnabled(false);
            new AlertDialog.Builder(requireActivity()).setTitle("Logout").setMessage("Do you want to logout ?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //disconnect to avoid multi instance when logged in again
                                    LogoutSequenceUtil.Logout((AppCompatActivity) requireActivity());
                                }

                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainXml.logoutBtn.setEnabled(true);
                            dialog.dismiss();

                        }
                    }).show();

        });
        mainXml.openPrivacySettingBtn.setOnClickListener(v->{
            clickInterface.onItemClick(mainXml.openPrivacySettingBtn);
        });
        mainXml.backBtn.setOnClickListener(v->{
            requireActivity().finish();
        });
    }
}