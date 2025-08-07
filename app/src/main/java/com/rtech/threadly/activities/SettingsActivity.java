package com.rtech.threadly.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.ActivitySettingsBinding;
import com.rtech.threadly.fragments.settingFragments.setting_main_fragments;
import com.rtech.threadly.utils.ReUsableFunctions;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding mainXml;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(0); // clear light status flag
            window.setStatusBarColor(Color.BLACK); // or any dark color you're using
        }


        openMain();



    }

    private void openMain() {
        getSupportFragmentManager().beginTransaction().replace(mainXml.settingContainer.getId(),new setting_main_fragments()).commit();
    }
}