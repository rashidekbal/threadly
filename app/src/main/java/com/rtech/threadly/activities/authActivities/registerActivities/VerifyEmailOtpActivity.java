package com.rtech.threadly.activities.authActivities.registerActivities;

import android.annotation.SuppressLint;
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
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.OtpManager;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyEmailOtpActivity extends AppCompatActivity {
    AppCompatButton next_btn;
    Intent intentData;
    EditText otp_field;
    TextView msg_textView;
    ProgressBar progressBar;
    OtpManager otpManager;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        next_btn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            next_btn.setText("");

            String otp=otp_field.getText().toString();
            if(otp.length()!=6){
                msg_textView.setVisibility(View.VISIBLE);
                msg_textView.setTextColor(Color.parseColor("#D00707"));
                msg_textView.setText("Otp must be 6-digit");
                progressBar.setVisibility(View.GONE);
                next_btn.setText("Next");

            }else{
                otpManager.VerifyOtpEmail(intentData.getStringExtra("email"), otp, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        next_btn.setText("Next");
                        next_btn.setEnabled(true);
                        try {
                            String token=response.getString("token");
                            Intent intent =new Intent(getApplicationContext(), CreatePasswordActivity.class);
                            intent.putExtra("token",token);
                            intent.putExtra("api", ApiEndPoints.REGISTER_EMAIL);
                            startActivity(intent);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(String err) {
                        int statusCode=Integer.parseInt(err);
                        msg_textView.setVisibility(View.VISIBLE);
                        msg_textView.setTextColor(Color.parseColor("#D00707"));
                        if(statusCode==401){
                            msg_textView.setText("Invalid otp");
                        }else{
                            msg_textView.setText("Something went wrong");

                        }

                    }
                });



            }

        });
    }
    protected  void init(){
        next_btn=findViewById(R.id.next_btn);
        otp_field=findViewById(R.id.otp_field);
        msg_textView=findViewById(R.id.msg_textView);
        progressBar=findViewById(R.id.progressBar);
        intentData=getIntent();
        otpManager=new OtpManager();
    }
}