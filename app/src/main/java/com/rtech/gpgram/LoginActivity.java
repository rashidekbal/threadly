package com.rtech.gpgram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class LoginActivity extends AppCompatActivity {
AppCompatButton login_btn,signup_btn;
EditText userid_field,password_filed;
SharedPreferences loginInfo;
SharedPreferences.Editor preferenceEditor;
ProgressBar progressBar;
String api_login_mobile=BuildConfig.BASE_URL.concat("/auth/login/mobile");
String api_login_email=BuildConfig.BASE_URL.concat("/auth/login/email");
String api_login_userid=BuildConfig.BASE_URL.concat("/auth/login/userid");

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



                    if(isEmail(userid)){
                     //executed code for emial login
                        Toast.makeText(LoginActivity.this,"email login feature not created yet",Toast.LENGTH_SHORT).show();
                        login_btn.setText("Log in");
                        progressBar.setVisibility(View.GONE);
                        login_btn.setEnabled(true);

                    }
                    else if (isPhone(userid)) {


                        JSONObject data=new JSONObject();
                        try {
                            data.put("userid",userid);
                            data.put("password",password);
                            AndroidNetworking.post(api_login_mobile)
                                    .setPriority(Priority.HIGH)
                                    .addApplicationJsonBody(data)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        String username=response.getString("username");
                                        String userid=response.getString("userid");
                                        String profileUrl=response.getString("profile");
                                        preferenceEditor.putString("token",response.getString("token"));
                                        preferenceEditor.putBoolean("isLoggedIn",true);
                                        preferenceEditor.putString("username",username);
                                        preferenceEditor.putString("userid",userid);
                                        preferenceEditor.putString("profileUrl",profileUrl);
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
                                public void onError(ANError anError) {
                                    int errorCode=anError.getErrorCode();
                                    login_btn.setEnabled(true);
                                    login_btn.setText("Log in");
                                    progressBar.setVisibility(View.GONE);
                                    showDialog();

                                }
                            });




                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                    else{
                        //executed code for userid login
                        Toast.makeText(LoginActivity.this,"userid login feature not created yet",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        login_btn.setText("Log in");
                        login_btn.setEnabled(true);


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
    }
    protected  void init(){
        login_btn=findViewById(R.id.login_btn);
        signup_btn=findViewById(R.id.SignUp_btn);
        userid_field=findViewById(R.id.userid_field);
        password_filed=findViewById(R.id.password_field);
        progressBar=findViewById(R.id.progressBar);
        loginInfo=getSharedPreferences("loginInfo",MODE_PRIVATE);
        preferenceEditor=loginInfo.edit();
        AndroidNetworking.initialize(LoginActivity.this);

    }

    private boolean isEmail(String input){
        return  input.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
    public boolean isPhone(String input) {
        return input.length() == 10 && input.matches("\\d+");
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