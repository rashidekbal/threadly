package com.rtech.threadly.activities.forgetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.ActivityForgetPasswordBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.OtpManager;
import com.rtech.threadly.utils.ReUsableFunctions;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding mainXml;
    // Declare UI components


    OtpManager otpManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXml=ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        otpManager = new OtpManager();
        mainXml.forgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forget password button click
                mainXml.forgetPasswordBtn.setEnabled(false);
                mainXml.forgetPasswordBtn.setText("");
                mainXml.progressBar.setVisibility(View.VISIBLE);

                String userid = mainXml.useridField.getText().toString().trim();
                if(ReUsableFunctions.isEmail(userid)){
                    otpManager.ForgetPasswordOptSendEmail(userid, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            // Hide progress bar and enable button
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.forgetPasswordBtn.setText(R.string.forget_password);
                            mainXml.forgetPasswordBtn.setEnabled(true);
                            // Handle success, e.g., navigate to OTP verification screen
                            ReUsableFunctions.ShowToast(ForgetPasswordActivity.this, "OTP sent to your Email");
                            // Navigate to OTP verification activity
                            Intent intent = new Intent(ForgetPasswordActivity.this, VerifyOtp_forgetPassword_Activity.class);
                            intent.putExtra("userid",userid);
                            intent.putExtra("type","email");
                            startActivity(intent);



                        }

                        @Override
                        public void onError(String err) {
                            int errorCode=Integer.parseInt(err);
                            if(errorCode==404){
                                mainXml.useridField.setError("User not found");
                            }else{
                                mainXml.useridField.setError("something went wrong");

                            }
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.forgetPasswordBtn.setText(R.string.forget_password);
                            mainXml.forgetPasswordBtn.setEnabled(true);


                        }
                    });

                } else if (ReUsableFunctions.isPhone(userid)) {
                    //if given userid is phone number
                    otpManager.ForgetPasswordOptSendMobile(userid, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            // Hide progress bar and enable button
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.forgetPasswordBtn.setText(R.string.forget_password);
                            mainXml.forgetPasswordBtn.setEnabled(true);
                            // Handle success, e.g., navigate to OTP verification screen
                            ReUsableFunctions.ShowToast(ForgetPasswordActivity.this, "OTP sent to your mobile number");
                            // Navigate to OTP verification activity
                             Intent intent = new Intent(ForgetPasswordActivity.this, VerifyOtp_forgetPassword_Activity.class);
                             intent.putExtra("userid",userid);
                             intent.putExtra("type","mobile");
                             startActivity(intent);



                        }

                        @Override
                        public void onError(String err) {
                            // Handle error, e.g., show a dialog or toast
                            ReUsableFunctions.ShowToast(ForgetPasswordActivity.this, err);
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.forgetPasswordBtn.setText(R.string.forget_password);
                            mainXml.forgetPasswordBtn.setEnabled(true);


                        }
                    });

                }else{
                    mainXml.useridField.setError("Invalid userid");
                    mainXml.progressBar.setVisibility(View.GONE);
                    mainXml.forgetPasswordBtn.setText(R.string.forget_password);
                    mainXml.forgetPasswordBtn.setEnabled(true);
                }

            }
        });

    }

}