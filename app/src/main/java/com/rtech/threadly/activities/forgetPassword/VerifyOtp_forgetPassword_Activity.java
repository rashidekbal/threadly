package com.rtech.threadly.activities.forgetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.OtpManager;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyOtp_forgetPassword_Activity extends AppCompatActivity {
    AppCompatButton verifyOtpButton;
    EditText otpField;
    ProgressBar progressBar;
    OtpManager otpManager;
    Intent PageDataIntent;
    String type;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();


        verifyOtpButton.setOnClickListener(v -> {
            // Handle OTP verification logic here
            String otp = otpField.getText().toString().trim();
            if (otp.isEmpty()) {
                otpField.setError("OTP cannot be empty");
            } else if (otp.length() < 6) {
                otpField.setError("OTP must be at least 6 digits");
            }
            else {
              // Hide the keyboard if it's open
                InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(otpField.getWindowToken(), 0);
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyOtpButton.setEnabled(false);
                verifyOtpButton.setText("");
                // Proceed with OTP verification
           if(type.equals("mobile")){
                   otpManager.VerifyOtpMobile(userid, otp, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                       @Override
                       public void onSuccess(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            verifyOtpButton.setEnabled(true);
                            verifyOtpButton.setText(R.string.verify_otp);
                           try {
                               String token=response.getString("token");
                                 Intent intent = new Intent(VerifyOtp_forgetPassword_Activity.this, ResetPasswordActivity.class);
                                    intent.putExtra("token", token);
                                    intent.putExtra("type", type);
                                    startActivity(intent);


                           } catch (JSONException e) {
                               throw new RuntimeException(e);
                           }
                       }

                       @Override
                       public void onError(String err) {
                            otpField.setError(err);
                           progressBar.setVisibility(View.GONE);
                           verifyOtpButton.setEnabled(true);
                           verifyOtpButton.setText(R.string.verify_otp);

                       }
                   });
                }else if(type.equals("email")){
               otpManager.VerifyOtpEmail(userid, otp, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                   @Override
                   public void onSuccess(JSONObject response) {
                       progressBar.setVisibility(View.GONE);
                       verifyOtpButton.setEnabled(true);
                       verifyOtpButton.setText(R.string.verify_otp);
                       try {
                           String token=response.getString("token");
                           Intent intent = new Intent(VerifyOtp_forgetPassword_Activity.this, ResetPasswordActivity.class);
                           intent.putExtra("token", token);
                           intent.putExtra("type", type);
                           startActivity(intent);


                       } catch (JSONException e) {
                           throw new RuntimeException(e);
                       }
                   }

                   @Override
                   public void onError(String err) {
                       otpField.setError(err);
                       progressBar.setVisibility(View.GONE);
                       verifyOtpButton.setEnabled(true);
                       verifyOtpButton.setText(R.string.verify_otp);

                   }
               });


           }
           }



        });
    }
    private void init() {
        verifyOtpButton = findViewById(R.id.verify_otp_btn);
        otpField = findViewById(R.id.otp_field);
        progressBar = findViewById(R.id.progressBar);
        otpManager = new OtpManager();
        PageDataIntent = getIntent();
        type = PageDataIntent.getStringExtra("type");
        userid = PageDataIntent.getStringExtra("userid");



    }
}