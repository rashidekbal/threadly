package com.rtech.threadly.activities.settings;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.PasswordChangeActivityBinding;
import com.rtech.threadly.fragments.settingFragments.Password_change_fragment;

public class PasswordChangeActivity extends AppCompatActivity {
    PasswordChangeActivityBinding mainXml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXml=PasswordChangeActivityBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        init();
        setClickHandlers();
    }

    private void setClickHandlers(){
        mainXml.backBtn.setOnClickListener(v->{super.onBackPressed();});
        mainXml.changePassword.setOnClickListener(v->{changeFragment(new Password_change_fragment(),null);});

    }
    private void init(){
        getSupportFragmentManager().addOnBackStackChangedListener(()->{
               if(getSupportFragmentManager().getBackStackEntryCount()==0){
                   mainXml.contentView.setVisibility(View.VISIBLE);
                   mainXml.fragmentContainer.setVisibility(View.GONE);
               }
        });
    }
    private void changeFragment(Fragment fragment,@Nullable String id){
        mainXml.fragmentContainer.setVisibility(View.VISIBLE);
        mainXml.contentView.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .replace(mainXml.fragmentContainer.getId(),fragment).addToBackStack(id).commit();

    }
}