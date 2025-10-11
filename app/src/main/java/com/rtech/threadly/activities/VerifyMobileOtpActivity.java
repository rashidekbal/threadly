package com.rtech.threadly.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rtech.threadly.R;
import com.rtech.threadly.constants.ApiEndPoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class VerifyMobileOtpActivity extends AppCompatActivity {
AppCompatButton next_btn,resend_btn;
TextView numberDetailsTxt,msgText;
ProgressBar progressBar,next_progressBar;
Intent getDataIntent;
EditText otp_field;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_mobile_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();//initialize all ids

        next_btn.setOnClickListener(v -> {
            next_btn.setEnabled(false);
            String otp=otp_field.getText().toString();
            if(otp.length()!=6){
                msgText.setVisibility(View.VISIBLE);
                msgText.setText("Please Enter 6 digit otp");
                next_btn.setEnabled(true);
            }else{
                msgText.setVisibility(View.GONE);
                next_btn.setText("");
                progressBar.setVisibility(View.VISIBLE);
                JSONObject body=new JSONObject();
                try {
                    body.put("phone",getDataIntent.getStringExtra("phone"));
                    body.put("otp",otp);

                    AndroidNetworking.post(ApiEndPoints.VERIFY_MOBILE_OTP).setPriority(Priority.HIGH).addApplicationJsonBody(body).build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            next_btn.setText("Next");
                            next_btn.setEnabled(true);
                            try {
                                String token= response.getString("token");
                                Intent intent =new Intent(getApplicationContext(), CreatePasswordActivity.class);
                                intent.putExtra("token",token);
                                intent.putExtra("api", ApiEndPoints.REGISTER_MOBILE);
                                startActivity(intent);




                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            int errorCode=anError.getErrorCode();
                            if(errorCode==401){
                                msgText.setText("Invalid otp");
                                msgText.setVisibility(View.VISIBLE);

                            }else{
                                msgText.setText("Something went wrong");
                                msgText.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                            next_btn.setText("Next");
                            next_btn.setEnabled(true);

                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }


        });
        numberDetailsTxt.setText("To confirm you account, enter the 6-digit code we sent via Whatsapp SMS to +91".concat(Objects.requireNonNull(getDataIntent.getStringExtra("phone"))));
        resend_btn.setOnClickListener(v -> {
            resend_btn.setEnabled(false);
            String phone=getDataIntent.getStringExtra("phone");
            resend_btn.setText("");
            next_progressBar.setVisibility(View.VISIBLE);
            JSONObject body=new JSONObject();
            try {
                body.put("phone",phone);
                AndroidNetworking.post(ApiEndPoints.RESEND_MOBILE_OTP)
                        .addApplicationJsonBody(body)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                next_progressBar.setVisibility(View.GONE);
                              resend_btn.setText("I didn't get the code");
                                Toast.makeText(VerifyMobileOtpActivity.this, "otp sent successfully", Toast.LENGTH_SHORT).show();
                                resend_btn.setEnabled(true);


                            }

                            @Override
                            public void onError(ANError anError) {
                                int errorCode=anError.getErrorCode();
                                Toast.makeText(getApplicationContext(), Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
                                next_progressBar.setVisibility(View.GONE);
                                resend_btn.setText("Next");
                                Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                                resend_btn.setEnabled(true);

                            }
                        });





            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



        });
    }

    protected  void init(){
        getDataIntent=getIntent();
        next_btn=findViewById(R.id.next_btn);
        resend_btn=findViewById(R.id.resend_otp_btn);
        next_progressBar=findViewById(R.id.progressBarResend);
        numberDetailsTxt=findViewById(R.id.dynamic_message_textView);
        otp_field=findViewById(R.id.otp_field);
        progressBar=findViewById(R.id.progressBar);
        msgText=findViewById(R.id.msg_textView);

    }
}