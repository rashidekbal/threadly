package com.rtech.gpgram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class VerifyMobileOtpActivity extends AppCompatActivity {
AppCompatButton next_btn,resend_btn;
TextView numberdetailstxt;
ProgressBar progressBar,next_progressBar;
Intent getDataIntent;
TextView otp_field;
String api=BuildConfig.BASE_URL.concat("/otp/verifyOtpMobile");
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
        AndroidNetworking.initialize(getApplicationContext());//initialize networking

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_btn.setEnabled(false);
                String otp=otp_field.getText().toString();
                if(otp.isEmpty()||otp.length()<6){
                    Toast.makeText(VerifyMobileOtpActivity.this, "please enter 6 digit otp", Toast.LENGTH_SHORT).show();
                    next_btn.setEnabled(true);
                }else{
                    next_btn.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject body=new JSONObject();
                    try {
                        body.put("phone",getDataIntent.getStringExtra("phone"));
                        body.put("otp",otp);

                        AndroidNetworking.post(api).setPriority(Priority.HIGH).addApplicationJsonBody(body).build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressBar.setVisibility(View.GONE);
                                next_btn.setText("Next");
                                next_btn.setEnabled(true);
                                JSONObject recieved=response;
                                try {
                                    String token=recieved.getString("token");
                                    Intent intent =new Intent(getApplicationContext(),CreatePasswordActivity.class);
                                    intent.putExtra("token",token);
                                    startActivity(intent);




                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                int errorcode=anError.getErrorCode();
                                Toast.makeText(getApplicationContext(), Integer.toString(errorcode), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                next_btn.setText("Next");
                                Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                                next_btn.setEnabled(true);

                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }


            }
        });
        numberdetailstxt.setText("To confirm you account, enter the 6-digit code we sent via Whatsapp SMS to +91".concat(getDataIntent.getStringExtra("phone")));
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend_btn.setEnabled(false);
                String phone=getDataIntent.getStringExtra("phone");
                resend_btn.setText("");
                next_progressBar.setVisibility(View.VISIBLE);
                JSONObject body=new JSONObject();
                try {
                    body.put("phone",phone);
                    AndroidNetworking.post(BuildConfig.BASE_URL.concat("/otp/generateOtpMobile"))
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
                                    int errorcode=anError.getErrorCode();
                                    Toast.makeText(getApplicationContext(), Integer.toString(errorcode), Toast.LENGTH_SHORT).show();
                                    next_progressBar.setVisibility(View.GONE);
                                    resend_btn.setText("Next");
                                    Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                                    resend_btn.setEnabled(true);

                                }
                            });





                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }



            }
        });
    }

    protected  void init(){
        getDataIntent=getIntent();
        next_btn=findViewById(R.id.next_btn);
        resend_btn=findViewById(R.id.resend_otp_btn);
        next_progressBar=findViewById(R.id.progressBarResend);
        numberdetailstxt=findViewById(R.id.dynamic_message_textView);
        otp_field=findViewById(R.id.otp_field);
        progressBar=findViewById(R.id.progressBar);

    }
}