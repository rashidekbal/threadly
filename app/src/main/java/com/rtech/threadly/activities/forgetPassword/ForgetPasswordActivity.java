package com.rtech.threadly.activities.forgetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

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

public class ForgetPasswordActivity extends AppCompatActivity {
    // Declare UI components
    AppCompatButton forgetPasswordButton;
    EditText useridField;
    OtpManager otpManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forget password button click
                forgetPasswordButton.setEnabled(false);
                forgetPasswordButton.setText("");
                progressBar.setVisibility(View.VISIBLE);

                String userid = useridField.getText().toString().trim();
                if(ReUsableFunctions.isEmail(userid)){


                    otpManager.ForgetPasswordOptSendEmail(userid, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            // Hide progress bar and enable button
                            progressBar.setVisibility(View.GONE);
                            forgetPasswordButton.setText(R.string.forget_password);
                            forgetPasswordButton.setEnabled(true);
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
                                useridField.setError("User not found");
                                progressBar.setVisibility(View.GONE);
                                forgetPasswordButton.setText(R.string.forget_password);
                                forgetPasswordButton.setEnabled(true);
                            }else{
                                useridField.setError("something went wrong");
                                progressBar.setVisibility(View.GONE);
                                forgetPasswordButton.setText(R.string.forget_password);
                                forgetPasswordButton.setEnabled(true);

                            }



                        }
                    });

                } else if (ReUsableFunctions.isPhone(userid)) {
                    //if given userid is phone number
                    otpManager.ForgetPasswordOptSendMobile(userid, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            // Hide progress bar and enable button
                            progressBar.setVisibility(View.GONE);
                            forgetPasswordButton.setText(R.string.forget_password);
                            forgetPasswordButton.setEnabled(true);
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
                            progressBar.setVisibility(View.GONE);
                            forgetPasswordButton.setText(R.string.forget_password);
                            forgetPasswordButton.setEnabled(true);


                        }
                    });

                }else{
                    useridField.setError("Invalid userid");
                    progressBar.setVisibility(View.GONE);
                    forgetPasswordButton.setText(R.string.forget_password);
                    forgetPasswordButton.setEnabled(true);
                }

            }
        });

    }
    private void init() {
        // Initialize UI components here if needed
        forgetPasswordButton = findViewById(R.id.forgetPassword_btn);
        useridField = findViewById(R.id.userid_field);
        progressBar = findViewById(R.id.progressBar);
        otpManager = new OtpManager();
    }
}