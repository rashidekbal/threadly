package com.rtech.threadly.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.rtech.threadly.R;
import com.rtech.threadly.activities.forgetPassword.ForgetPasswordActivity;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.managers.AuthManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
AppCompatButton login_btn,signup_btn;
EditText userid_field,password_filed;
SharedPreferences loginInfo;
SharedPreferences.Editor preferenceEditor;
ProgressBar progressBar;
TextView forgetPassword_btn;
AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn.setEnabled(false);
                String userid=userid_field.getText().toString().trim();
                String password=password_filed.getText().toString().trim();
                if(userid.isEmpty()||password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "please fill the required field", Toast.LENGTH_SHORT).show();
                    login_btn.setEnabled(true);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    login_btn.setText("");



                    if(ReUsableFunctions.isEmail(userid)){
                        authManager.LoginEmail(userid, password, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                String username= null;
                                try {
                                    username = response.getString("username");
                                    String userid=response.getString("userid");
                                    String profileUrl=response.getString("profile");
                                    preferenceEditor.putString(SharedPreferencesKeys.JWT_TOKEN,response.getString("token"));
                                    preferenceEditor.putBoolean(SharedPreferencesKeys.IS_LOGGED_IN,true);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_NAME,username);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_ID,userid);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_PROFILE_PIC,profileUrl);
                                    preferenceEditor.apply();
                                    Intent homePage=new Intent(LoginActivity.this,HomeActivity.class);
                                    homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(homePage);
                                    finish();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }

                            @Override
                            public void onError(String err) {
                                int errorCode=Integer.parseInt(err);
                                login_btn.setEnabled(true);
                                login_btn.setText("Log in");
                                progressBar.setVisibility(View.GONE);
                                showDialog();

                            }
                        });


                    }
                    else if (ReUsableFunctions.isPhone(userid)) {
                        authManager.LoginMobile(userid, password, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                String username= null;
                                try {
                                    username = response.getString("username");
                                    String userid=response.getString("userid");
                                    String profileUrl=response.getString("profile");
                                    preferenceEditor.putString(SharedPreferencesKeys.JWT_TOKEN,response.getString("token"));
                                    preferenceEditor.putBoolean(SharedPreferencesKeys.IS_LOGGED_IN,true);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_NAME,username);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_ID,userid);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_PROFILE_PIC,profileUrl);
                                    preferenceEditor.apply();
                                    Intent homePage=new Intent(LoginActivity.this,HomeActivity.class);
                                    homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(homePage);
                                    finish();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }

                            @Override
                            public void onError(String err) {
                                int errorCode=Integer.parseInt(err);
                                login_btn.setEnabled(true);
                                login_btn.setText("Log in");
                                progressBar.setVisibility(View.GONE);
                                showDialog();

                            }
                        });

                    }
                    else{

                        authManager.LoginUserId(userid, password, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                String username= null;
                                try {
                                    username = response.getString("username");
                                    String userid=response.getString("userid");
                                    String profileUrl=response.getString("profile");
                                    preferenceEditor.putString(SharedPreferencesKeys.JWT_TOKEN,response.getString("token"));
                                    preferenceEditor.putBoolean(SharedPreferencesKeys.IS_LOGGED_IN,true);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_NAME,username);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_ID,userid);
                                    preferenceEditor.putString(SharedPreferencesKeys.USER_PROFILE_PIC,profileUrl);
                                    preferenceEditor.apply();
                                    Intent homePage=new Intent(LoginActivity.this,HomeActivity.class);
                                    homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(homePage);
                                    finish();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }

                            @Override
                            public void onError(String err) {
                                int errorCode=Integer.parseInt(err);
                                login_btn.setEnabled(true);
                                login_btn.setText("Log in");
                                progressBar.setVisibility(View.GONE);
                                showDialog();

                            }
                        });


                    }

                }






            }
        });
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpMobileActivity.class));
            }
        });
        forgetPassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
    }
    protected  void init(){
        login_btn=findViewById(R.id.login_btn);
        signup_btn=findViewById(R.id.SignUp_btn);
        userid_field=findViewById(R.id.userid_field);
        password_filed=findViewById(R.id.password_field);
        progressBar=findViewById(R.id.progressBar);
        loginInfo= Core.getPreference();
        preferenceEditor=loginInfo.edit();
        forgetPassword_btn=findViewById(R.id.forgetPassword_btn);
        authManager=new AuthManager();

    }


    private void showDialog(){
        AlertDialog dialog=new AlertDialog.Builder(LoginActivity.this).create();
        dialog.setTitle("That login info didn't work");
        dialog.setMessage("Check your username, mobile number, email or password");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,"ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        dialog.show();

    }
}