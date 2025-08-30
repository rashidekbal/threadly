package com.rtech.threadly.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityMessagePageBinding;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class MessagePageActivity extends AppCompatActivity {
    ActivityMessagePageBinding mainXml;
    Bundle userdata;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainXml=ActivityMessagePageBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userdata=getIntent().getExtras();
        init();

    }
    private void init(){
        if(userdata!=null){
            mainXml.usename.setText(userdata.getString("username"));
            Glide.with(MessagePageActivity.this).load(userdata.getString("profilePic")).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profile);
            uuid=userdata.getString("uuid");
        }
        mainXml.sendBtn.setOnClickListener(
                v->{
                    String msg=mainXml.msgField.getText().toString().trim();
                    if(!msg.isEmpty()){
                        try {
                            SendMsg(uuid,msg);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        mainXml.msgField.setText("");
                    }
                }
        );




    }
        public static void SendMsg(String userId,String msg) throws JSONException {
        JSONObject object=new JSONObject();
        object.put("senderUuid",Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"));
        object.put("receiverUuid",userId);
        object.put("senderName",Core.getPreference().getString(SharedPreferencesKeys.USER_NAME,"null"));
        object.put("senderUserId",Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"));
        object.put("senderProfilePic",Core.getPreference().getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null"));
        object.put("msg",msg);

            SocketManager.getInstance().getSocket().emit("CToS",object);
            ReUsableFunctions.ShowToast("message sent");

    }
}