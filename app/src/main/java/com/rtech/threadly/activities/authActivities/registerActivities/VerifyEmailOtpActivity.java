package com.rtech.threadly.activities.authActivities.registerActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.constants.ApiEndPoints;
import com.rtech.threadly.databinding.ActivityVerifyEmailOtpBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.OtpManager;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyEmailOtpActivity extends AppCompatActivity {
 ActivityVerifyEmailOtpBinding mainXMl;
 OtpManager otpManager;
 Intent intentData;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXMl=ActivityVerifyEmailOtpBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainXMl.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(mainXMl.main.getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();


    }

    private void handleVerify(){
        mainXMl.progressBar.setVisibility(View.VISIBLE);
        mainXMl.nextBtn.setText("");

        String otp=mainXMl.otpField.getText().toString();
        if(otp.length()!=6){
            mainXMl.msgTextView.setVisibility(View.VISIBLE);
            mainXMl.msgTextView.setTextColor(Color.parseColor("#D00707"));
            mainXMl.msgTextView.setText("Otp must be 6-digit");
            mainXMl.msgTextView.setVisibility(View.GONE);
            mainXMl.msgTextView.setText("Next");

        }else{
            otpManager.VerifyOtpEmail(intentData.getStringExtra("email"), otp, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                @Override
                public void onSuccess(JSONObject response) {
                    mainXMl.progressBar.setVisibility(View.GONE);
                    mainXMl.nextBtn.setText("Next");
                    mainXMl.nextBtn.setEnabled(true);
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
                    mainXMl.msgTextView.setVisibility(View.VISIBLE);
                    mainXMl.msgTextView.setTextColor(Color.parseColor("#D00707"));
                    if(statusCode==401){
                        mainXMl.msgTextView.setText("Invalid otp");
                    }else{
                        mainXMl.msgTextView.setText("Something went wrong");

                    }

                }
            });



        }
    }
    private void handleOtpResend(){
        mainXMl.resendOtpBtn.setEnabled(false);
        String email=intentData.getStringExtra("email");
        mainXMl.resendOtpBtn.setText("");
        mainXMl.progressBarResend.setVisibility(View.VISIBLE);
        JSONObject body=new JSONObject();
        try {
            body.put("email",email);
            AndroidNetworking.post(ApiEndPoints.RESEND_EMAIL_OTP)
                    .addApplicationJsonBody(body)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mainXMl.progressBarResend.setVisibility(View.GONE);
                            mainXMl.resendOtpBtn.setText("I didn't get the code");
                            Toast.makeText(VerifyEmailOtpActivity.this, "otp sent successfully", Toast.LENGTH_SHORT).show();
                            mainXMl.resendOtpBtn.setEnabled(true);


                        }

                        @Override
                        public void onError(ANError anError) {
                            int errorCode=anError.getErrorCode();
                            Toast.makeText(getApplicationContext(), Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
                            mainXMl.progressBarResend.setVisibility(View.GONE);
                            mainXMl.resendOtpBtn.setText("Next");
                            Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                            mainXMl.resendOtpBtn.setEnabled(true);

                        }
                    });





        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    private void setOnclickListeners() {
        mainXMl.nextBtn.setOnClickListener(v -> handleVerify());
        mainXMl.resendOtpBtn.setOnClickListener(v -> handleOtpResend());
    }
    protected  void init(){
        intentData=getIntent();
        otpManager=new OtpManager();
        setOnclickListeners();
    }
}