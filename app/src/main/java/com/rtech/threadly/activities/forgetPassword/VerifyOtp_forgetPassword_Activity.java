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
import com.rtech.threadly.databinding.ActivityVerifyOtpForgetPasswordBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.OtpManager;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyOtp_forgetPassword_Activity extends AppCompatActivity {
    ActivityVerifyOtpForgetPasswordBinding mainXml;
    OtpManager otpManager;
    Intent PageDataIntent;
    String type;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXml=ActivityVerifyOtpForgetPasswordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();


        mainXml.verifyOtpBtn.setOnClickListener(v -> {
            // Handle OTP verification logic here
            String otp = mainXml.otpField.getText().toString().trim();
            if (otp.isEmpty()) {
                mainXml.otpField.setError("OTP cannot be empty");
            } else if (otp.length() < 6) {
                mainXml.otpField.setError("OTP must be at least 6 digits");
            }
            else {
              // Hide the keyboard if it's open
                InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(mainXml.otpField.getWindowToken(), 0);
                }
                mainXml.progressBar.setVisibility(View.VISIBLE);
                mainXml.verifyOtpBtn.setEnabled(false);
                mainXml.verifyOtpBtn.setText("");
                // Proceed with OTP verification
           if(type.equals("mobile")){
                   otpManager.VerifyOtpMobile(userid, otp, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                       @Override
                       public void onSuccess(JSONObject response) {
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.verifyOtpBtn.setEnabled(true);
                            mainXml.verifyOtpBtn.setText(R.string.verify_otp);
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
                            mainXml.otpField.setError(err);
                           mainXml.progressBar.setVisibility(View.GONE);
                           mainXml.verifyOtpBtn.setEnabled(true);
                           mainXml.verifyOtpBtn.setText(R.string.verify_otp);

                       }
                   });
                }else if(type.equals("email")){
               otpManager.VerifyOtpEmail(userid, otp, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                   @Override
                   public void onSuccess(JSONObject response) {
                       mainXml.progressBar.setVisibility(View.GONE);
                       mainXml.verifyOtpBtn.setEnabled(true);
                       mainXml.verifyOtpBtn.setText(R.string.verify_otp);
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
                       mainXml.otpField.setError(err);
                       mainXml.progressBar.setVisibility(View.GONE);
                       mainXml.verifyOtpBtn.setEnabled(true);
                       mainXml.verifyOtpBtn.setText(R.string.verify_otp);

                   }
               });


           }
           }



        });
    }
    private void init() {

        otpManager = new OtpManager();
        PageDataIntent = getIntent();
        type = PageDataIntent.getStringExtra("type");
        userid = PageDataIntent.getStringExtra("userid");



    }
}