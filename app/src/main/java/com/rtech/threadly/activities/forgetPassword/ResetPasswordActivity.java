package com.rtech.threadly.activities.forgetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rtech.threadly.R;
import com.rtech.threadly.activities.LoginActivity;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.AuthManager;
import com.rtech.threadly.utils.ReUsableFunctions;

public class ResetPasswordActivity extends AppCompatActivity {
    TextView password_Text_field;
    AppCompatButton resetPasswordButton;
    ProgressBar progressBar;
    AuthManager authManager;
    Intent PageDataIntent;
    String token,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = password_Text_field.getText().toString().trim();
                if (password.isEmpty()) {
                    password_Text_field.setError("Password cannot be empty");

                } else if (password.length()<6) {
                    password_Text_field.setError("Password must be at least 6 characters long");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPasswordButton.setEnabled(false);
                    resetPasswordButton.setText("");
                    if(type.equals("mobile")){
                        authManager.ResetPasswordWithMobile(password, token, new NetworkCallbackInterface() {
                            @Override
                            public void onSuccess() {
                                ReUsableFunctions.ShowToast(ResetPasswordActivity.this, "Password reset successfully");
                                progressBar.setVisibility(View.GONE);
                                resetPasswordButton.setEnabled(true);
                                resetPasswordButton.setText("Reset Password");
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                            @Override
                            public void onError(String err) {
                                progressBar.setVisibility(View.GONE);
                                resetPasswordButton.setEnabled(true);
                                resetPasswordButton.setText("Reset Password");

                            }
                        });
                    }
                    else if (type.equals("email")) {
                        authManager.ResetPasswordWithEmail(password, token, new NetworkCallbackInterface() {
                            @Override
                            public void onSuccess() {
                                ReUsableFunctions.ShowToast(ResetPasswordActivity.this, "Password reset successfully");
                                progressBar.setVisibility(View.GONE);
                                resetPasswordButton.setEnabled(true);
                                resetPasswordButton.setText("Reset Password");
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                            @Override
                            public void onError(String err) {
                                progressBar.setVisibility(View.GONE);
                                resetPasswordButton.setEnabled(true);
                                resetPasswordButton.setText("Reset Password");

                            }
                        });


                    }


                }

            }
        });

    }

    private void init() {
        password_Text_field = findViewById(R.id.password_Text_field);
        resetPasswordButton = findViewById(R.id.reset_password_btn);
        progressBar= findViewById(R.id.progressBar);
        authManager = new AuthManager();
        PageDataIntent = getIntent();
        token= PageDataIntent.getStringExtra("token");
        type = PageDataIntent.getStringExtra("type");


    }
}