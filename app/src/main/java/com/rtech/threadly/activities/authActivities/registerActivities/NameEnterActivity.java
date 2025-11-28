package com.rtech.threadly.activities.authActivities.registerActivities;

import android.annotation.SuppressLint;
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
import com.rtech.threadly.R;
import com.rtech.threadly.activities.HomeActivity;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class NameEnterActivity extends AppCompatActivity {
ProgressBar progressBar;
EditText name_field;
AppCompatButton next_btn;
Intent getDataIntent;
String api;
SharedPreferences loginPreference;
SharedPreferences.Editor preferenceEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_name_enter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        next_btn.setOnClickListener(v -> {
            next_btn.setEnabled(false);
            String name=name_field.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(NameEnterActivity.this, "please enter your full name", Toast.LENGTH_SHORT).show();
                next_btn.setEnabled(true);
            }else{
                JSONObject data=new JSONObject();
                try {
                    data.put("password",getDataIntent.getStringExtra("password"));
                    data.put("dob",getDataIntent.getStringExtra("dob"));
                    data.put("username",name);


                    progressBar.setVisibility(View.VISIBLE);
                    next_btn.setText("");
                    AndroidNetworking.post(api).addHeaders("Authorization", "Bearer ".
                            concat(Objects.requireNonNull(getDataIntent.getStringExtra("token"))))
                            .addApplicationJsonBody(data)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            next_btn.setText("Next");
                            try {

                                String username=response.getString("username");
                                String userid=response.getString("userid");
                                String profileUrl=response.getString("profile");
                                preferenceEditor.putString("token",response.getString("token"));
                                preferenceEditor.putBoolean("isLoggedIn",true);
                                preferenceEditor.putString("username",username);
                                preferenceEditor.putString("userid",userid);
                                preferenceEditor.putString("profileUrl",profileUrl);
                                preferenceEditor.putString(SharedPreferencesKeys.UUID,response.getString("uuid"));
                                preferenceEditor.apply();
                                Toast.makeText(NameEnterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                Intent homePage=new Intent(NameEnterActivity.this, HomeActivity.class);
                                homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homePage);
                                Core.startSocketEvents();
                                ReUsableFunctions.updateFcmTokenToServer();
                                finish();

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }





                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onError(ANError anError) {
                            int errorCode=anError.getErrorCode();
                            progressBar.setVisibility(View.GONE);
                            next_btn.setText("Next");
                            Toast.makeText(NameEnterActivity.this, Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
                            next_btn.setEnabled(true);

                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }




            }
        });
    }
    protected void init(){
        getDataIntent=getIntent();
        AndroidNetworking.initialize(NameEnterActivity.this);
        progressBar=findViewById(R.id.progressBar);
        name_field=findViewById(R.id.name_field);
        next_btn=findViewById(R.id.next_btn);
        loginPreference= Core.getPreference();
        preferenceEditor=loginPreference.edit();
        api=getDataIntent.getStringExtra("api");


    }
}