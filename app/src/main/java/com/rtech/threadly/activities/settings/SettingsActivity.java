package com.rtech.threadly.activities.settings;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.ActivitySettingsBinding;
import com.rtech.threadly.fragments.settingFragments.ControlCenterFragment;
import com.rtech.threadly.fragments.settingFragments.PrivacySetting_fragment;
import com.rtech.threadly.utils.LogoutSequenceUtil;
import com.rtech.threadly.viewmodels.ProfileViewModel;

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
        profileViewModel.getProfileLiveData().observe(this,profileModel -> {
            if(profileModel!=null){
                mainXml.progressBar.setVisibility(View.GONE);
                mainXml.optionsList.setVisibility(View.VISIBLE);


            }
        });
        setFragmentChangeListener();
        setPageOpenerHandler();

    }
    private void setPageOpenerHandler(){
        mainXml.accountCenter.setOnClickListener(v->{changeFragment(new ControlCenterFragment());});
        mainXml.openPrivacySettingBtn.setOnClickListener(v->changeFragment(new PrivacySetting_fragment()));
        mainXml.logoutBtn.setOnClickListener(v->{handleLogout();});
        mainXml.backBtn.setOnClickListener(v->super.onBackPressed());

    }
    private void setFragmentChangeListener(){
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount()==0){
                    mainXml.settingPageContainer.setVisibility(View.GONE);
                    return ;
                }
                mainXml.settingPageContainer.setVisibility(View.VISIBLE);

            }
        });
    }
    private void changeFragment(Fragment fragment){

        getSupportFragmentManager().beginTransaction().replace(mainXml.settingPageContainer.getId(),fragment).addToBackStack(null).commit();
    }
    private void handleLogout(){
        new AlertDialog.Builder(this).setTitle("Logout").setMessage("Do you want to logout ?")
                .setPositiveButton("yes", (dialog, which) -> {
                    dialog.dismiss();
                    LogoutSequenceUtil.Logout(this);
                }).setNegativeButton("no", (dialog, which) -> {
                    mainXml.logoutBtn.setEnabled(true);
                    dialog.dismiss();
                }).show();
    }
}
