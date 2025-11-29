package com.rtech.threadly.activities.authActivities.registerActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;

public class CreatePasswordActivity extends AppCompatActivity {
AppCompatButton next_btn;
EditText password_field;
Intent getDataIntent;
String token;
TextView msgText;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        next_btn.setOnClickListener(v -> {
            next_btn.setEnabled(false);
            String password=password_field.getText().toString().trim();
            if(password.isEmpty()||password.length()<6){
                msgText.setVisibility(View.VISIBLE);
                msgText.setText("Password must be more than 6 characters long");
                next_btn.setEnabled(true);
            }else{
                msgText.setVisibility(View.GONE);
                next_btn.setEnabled(true);
                Intent intent=new Intent(getApplicationContext(), EnterDobActivity.class);
                intent.putExtra("token",token);
                intent.putExtra("password",password);
                intent.putExtra("api",getDataIntent.getStringExtra("api"));
                startActivity(intent);

            }

        });
    }
    protected void init(){
        next_btn=findViewById(R.id.next_btn);
        password_field=findViewById(R.id.password_Text_field);
        getDataIntent=getIntent();
        token=getDataIntent.getStringExtra("token");
        msgText=findViewById(R.id.msg_textView);
    }
}