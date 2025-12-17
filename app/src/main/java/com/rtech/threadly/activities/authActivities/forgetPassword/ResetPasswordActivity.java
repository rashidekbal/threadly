package com.rtech.threadly.activities.authActivities.forgetPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;
import com.rtech.threadly.activities.authActivities.loginActivities.LoginActivity;
import com.rtech.threadly.databinding.ActivityResetPasswordBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.AuthManager;
import com.rtech.threadly.utils.ReUsableFunctions;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding mainXml;
    AuthManager authManager;
    Intent PageDataIntent;
    String token,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXml=ActivityResetPasswordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        mainXml.resetPasswordBtn.setOnClickListener(v -> {
            String password = mainXml.passwordTextField.getText().toString().trim();
            if (password.isEmpty()) {
                mainXml.passwordTextField.setError("Password cannot be empty");

            } else if (password.length()<6) {
                mainXml.passwordTextField.setError("Password must be at least 6 characters long");
            } else {
                mainXml.progressBar.setVisibility(View.VISIBLE);
                mainXml.resetPasswordBtn.setEnabled(false);
                mainXml.resetPasswordBtn.setText("");
                if(type.equals("mobile")){
                    authManager.ForgetPasswordWithMobile(password, token, new NetworkCallbackInterface() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess() {
                            ReUsableFunctions.ShowToast(ResetPasswordActivity.this, "Password reset successfully");
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.resetPasswordBtn.setEnabled(true);
                            mainXml.resetPasswordBtn.setText("Reset Password");
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onError(String err) {
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.resetPasswordBtn.setEnabled(true);
                            mainXml.resetPasswordBtn.setText("Reset Password");

                        }
                    });
                }
                else if (type.equals("email")) {
                    authManager.ForgetPasswordWithEmail(password, token, new NetworkCallbackInterface() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess() {
                            ReUsableFunctions.ShowToast(ResetPasswordActivity.this, "Password reset successfully");
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.resetPasswordBtn.setEnabled(true);
                            mainXml.resetPasswordBtn.setText("Reset Password");
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onError(String err) {
                            mainXml.progressBar.setVisibility(View.GONE);
                            mainXml.resetPasswordBtn.setEnabled(true);
                            mainXml.resetPasswordBtn.setText("Reset Password");

                        }
                    });


                }


            }

        });

    }

    private void init() {
        authManager = new AuthManager();
        PageDataIntent = getIntent();
        token= PageDataIntent.getStringExtra("token");
        type = PageDataIntent.getStringExtra("type");


    }
}