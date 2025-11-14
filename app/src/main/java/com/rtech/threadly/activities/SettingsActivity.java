package com.rtech.threadly.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.ActivitySettingsBinding;
import com.rtech.threadly.fragments.settingFragments.PrivacySetting_fragment;
import com.rtech.threadly.fragments.settingFragments.Setting_main_fragments;
import com.rtech.threadly.interfaces.FragmentItemClickInterface;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import java.security.Provider;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding mainXml;
    ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainXml=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(0); // clear light status flag
        window.setStatusBarColor(Color.BLACK); // or any dark color you're using
        profileViewModel= new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.loadProfile();

        openMain();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount()==0){
                    finish();}
            }
        });



    }

    private void openMain() {
        changeFragment(new Setting_main_fragments(new FragmentItemClickInterface() {
            @Override
            public void onItemClick(@Nullable View v) {
                assert v != null;
                if(v.getId()==R.id.openPrivacySettingBtn) {
                    // privacy setting
                    changeFragment(new PrivacySetting_fragment());

                }

            }

            @Override
            public void onFragmentDestroy() {

            }
        }));

    }
    private void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(mainXml.settingContainer.getId(),fragment).addToBackStack(null).commit();
    }
}
