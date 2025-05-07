package com.rtech.gpgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
SharedPreferences loginInfo;
SharedPreferences.Editor prefEditor;
Button logOut_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        TextView textView=findViewById(R.id.textView);
        String username=loginInfo.getString("username","madherchod");
        textView.setText(username);
        logOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefEditor.clear().apply();
                Intent loginPage=new Intent(HomeActivity.this, LoginActivity.class);
                loginPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginPage);
                finish();

            }
        });

    }
    protected void init(){
        loginInfo=getSharedPreferences("loginInfo", MODE_PRIVATE);
        prefEditor=loginInfo.edit();
        logOut_btn=findViewById(R.id.logout);


    }

}