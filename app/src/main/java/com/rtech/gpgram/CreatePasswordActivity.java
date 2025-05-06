package com.rtech.gpgram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreatePasswordActivity extends AppCompatActivity {
AppCompatButton next_btn;
EditText password_field;
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
        Intent intent =getIntent();
        String token=intent.getStringExtra("token");
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=password_field.getText().toString();
                Intent intent=new Intent(getApplicationContext(),EnterDobActivity.class);
                intent.putExtra("token",token);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });
    }
    protected void init(){
        next_btn=findViewById(R.id.next_btn);
        password_field=findViewById(R.id.password_Text_field);
    }
}