package com.rtech.threadly.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.OtpManager;
import com.rtech.threadly.utils.ReUsableFunctions;

public class SignUpEmailActivity extends AppCompatActivity {
AppCompatButton signUp_with_mobile_btn,next_btn;
EditText email_edittext;
TextView msg_textView;
ProgressBar progressBar;
OtpManager otpManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        signUp_with_mobile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_btn.setEnabled(false);
                next_btn.setText("");
                progressBar.setVisibility(View.VISIBLE);
                String email=email_edittext.getText().toString();
                boolean isValidEmail= ReUsableFunctions.isEmail(email);
                if(!isValidEmail){
                    msg_textView.setTextColor(Color.parseColor("#D00707"));
                    msg_textView.setText("Please enter a valid email");
                    next_btn.setEnabled(true);
                    next_btn.setText(R.string.next);
                    progressBar.setVisibility(View.GONE);

                }else{
                    otpManager.SendOtpEmail(email, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            msg_textView.setText(R.string.email_info_phrase);
                            next_btn.setEnabled(true);
                            next_btn.setText(R.string.next);
                            progressBar.setVisibility(View.GONE);
                            email_edittext.setText("");
                            Intent intent=new Intent(SignUpEmailActivity.this,VerifyEmailOtpActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);


                        }

                        @Override
                        public void onError(String err) {
                            int ErrorCode=Integer.parseInt(err);
                            switch (ErrorCode){
                                case 400:
                                    msg_textView.setTextColor(Color.parseColor("#D00707"));
                                    msg_textView.setText("Invalid Email");
                                    next_btn.setEnabled(true);
                                    next_btn.setText(R.string.next);
                                    progressBar.setVisibility(View.GONE);
                                    break;
                                case 409:
                                    msg_textView.setTextColor(Color.parseColor("#D00707"));
                                    msg_textView.setText("Email already registered please login");
                                    next_btn.setEnabled(true);
                                    next_btn.setText(R.string.next);
                                    progressBar.setVisibility(View.GONE);
                                    break;
                                case 500:
                                    msg_textView.setTextColor(Color.parseColor("#D00707"));
                                    msg_textView.setText("Something went Wrong...");
                                    next_btn.setEnabled(true);
                                    next_btn.setText(R.string.next);
                                    progressBar.setVisibility(View.GONE);
                                    break;
                                    default: break;


                            }


                        }
                    });



                }
//                Intent intent=new Intent(SignUpEmailActivity.this,VerifyEmailOtpActivity.class);
//                startActivity(intent);
            }
        });
    }
    protected void init(){
        signUp_with_mobile_btn=findViewById(R.id.SignUp_with_mobile_btn);
        next_btn=findViewById(R.id.next_btn);
        email_edittext=findViewById(R.id.email_edittext);
        msg_textView=findViewById(R.id.msg_textView);
        progressBar=findViewById(R.id.progressBar);
        otpManager=new OtpManager();
    }
}