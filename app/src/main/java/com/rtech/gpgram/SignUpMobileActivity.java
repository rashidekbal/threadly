package com.rtech.gpgram;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpMobileActivity extends AppCompatActivity {
AppCompatButton Signup_withEmail,next_btn;
EditText phone_field;
ProgressBar progressBar;
String api= BuildConfig.BASE_URL.concat("/otp/generateOtpMobile");
TextView msgTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_mobile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init(); // initialize all the ids
        AndroidNetworking.initialize(getApplicationContext());//initialize networking
        // navigate to next sign up with email page
        Signup_withEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpMobileActivity.this, SignUpEmailActivity.class));
            }
        });
        
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_btn.setEnabled(false);
                next_btn.setText("");
                progressBar.setVisibility(View.VISIBLE);
                String phone =phone_field.getText().toString().trim();

                if(phone.isEmpty()||phone.length()<10){
                    if(phone.length()<10){
                        msgTextView.setText(R.string.number_lessthan_10_err);
                        msgTextView.setTextColor(Color.parseColor("#D00707"));
                        phone_field.setText("");
                    }
                   if(phone.isEmpty()){
                       msgTextView.setText(R.string.empty_number);
                       msgTextView.setTextColor(Color.parseColor("#D00707"));


                   }

                    progressBar.setVisibility(View.GONE);
                    next_btn.setText("Next");
                    next_btn.setEnabled(true);

                }else {
                    msgTextView.setText(R.string.phone_number_info_phrase);
                    msgTextView.setTextColor(Color.parseColor("#716F6F"));
                    JSONObject body=new JSONObject();
                    try {
                        body.put("phone",phone.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    AndroidNetworking.post(api)
                            .addApplicationJsonBody(body).setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int statusCode=response.getInt("statuscode");
                                if(statusCode==200){
                                    progressBar.setVisibility(View.GONE);
                                    next_btn.setText("Next");
                                    next_btn.setEnabled(true);
                                    Intent intent =new Intent(getApplicationContext(), VerifyMobileOtpActivity.class);
                                    intent.putExtra("phone",phone);
                                    startActivity(intent);

                                }else{
                                    msgTextView.setText("Enter a valid whatsapp number");
                                    msgTextView.setTextColor(Color.parseColor("#D00707"));
                                    progressBar.setVisibility(View.GONE);
                                    next_btn.setText("Next");
                                    next_btn.setEnabled(true);

                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }



                        }

                        @Override
                        public void onError(ANError anError) {
                            int errorcode=anError.getErrorCode();
                            if(errorcode==500){
                                msgTextView.setText("Something went wrong");
                                msgTextView.setTextColor(Color.parseColor("#D00707"));

                            }
                            if (errorcode==409){
                                msgTextView.setText("User Already exists");
                                msgTextView.setTextColor(Color.parseColor("#D00707"));
                            }
                            if(errorcode==400){
                                msgTextView.setText("Something went wrong");
                                msgTextView.setTextColor(Color.parseColor("#D00707"));
                            }
                            progressBar.setVisibility(View.GONE);
                            next_btn.setText("Next");
                            next_btn.setEnabled(true);

                        }
                    });
                }


            }
        });
    }
    protected void init(){
        Signup_withEmail=findViewById(R.id.SignUp_with_email_btn);
        next_btn=findViewById(R.id.next_btn);
        phone_field=findViewById(R.id.phone_field);
        progressBar=findViewById(R.id.progressBar);
        msgTextView=findViewById(R.id.msg_textView);
    }

}