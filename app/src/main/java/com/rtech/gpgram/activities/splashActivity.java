package com.rtech.gpgram.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.gpgram.R;

@SuppressLint("CustomSplashScreen")
public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN);

        SharedPreferences loginPreference=getSharedPreferences("loginInfo",MODE_PRIVATE);
        boolean isLoggedIn=loginPreference.getBoolean("isLoggedIn",false);
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(!isLoggedIn){
                startActivity(new Intent(splashActivity.this,LoginActivity.class));
                finish();
            }else{
                startActivity(new Intent(splashActivity.this,HomeActivity.class));
                finish();
            }

        }
    },1500);
    }
}