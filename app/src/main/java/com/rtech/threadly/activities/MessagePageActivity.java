package com.rtech.threadly.activities;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Database;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.SocketIo.SocketEmitterEvents;
import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.adapters.messanger.MessageAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityMessagePageBinding;
import com.rtech.threadly.network_managers.MessageManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessagesViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessagePageActivity extends AppCompatActivity {
    ActivityMessagePageBinding mainXml;
    Bundle userdata;
    String uuid;
    String conversationId;
    MessagesViewModel messagesViewModel;
    List<MessageSchema> msgList;
    MessageAdapter messageAdapter;

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
        messagesViewModel=new ViewModelProvider(this).get(MessagesViewModel.class);
        init();
        setUpRecyclerView();

        messagesViewModel.getMessages(conversationId).observe(this, new Observer<List<MessageSchema>>() {
            @Override
            public void onChanged(List<MessageSchema> messageSchemas) {
                if(!messageSchemas.isEmpty()){
                    msgList.clear();
                    msgList.addAll(messageSchemas);
                    messageAdapter.notifyItemChanged(msgList.size()-1);
                    mainXml.RecyclerView.scrollToPosition(msgList.size()-1);
                }


            }
        });
        mainXml.backBtn.setOnClickListener(v->{
            finish();
        });

    }

    private void init(){
        if(userdata!=null){
            msgList=new ArrayList<>();
            uuid=userdata.getString("uuid");
            conversationId=uuid+Core.getPreference().getString(SharedPreferencesKeys.UUID,null);
            mainXml.username.setText(userdata.getString("username"));
            mainXml.userId.setText(userdata.getString("userid"));
//            //update database and set message seen
            messagesViewModel.getConversationUnreadMsg_count(conversationId,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null")).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer>0){
                        //new unread message arrived or already exists
                        // update local db as seen and send message to global db to set as seen
                        try {
                            //to be replaced by socket driven handler

                            if(SocketManager.getInstance().getSocket().connected()){
                                SocketEmitterEvents.UpdateSeenMsg_status(uuid,Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"));
                            }else{
                                                            MessageManager.setSeenMessage(uuid,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                DataBase.getInstance().dao().updateMessagesSeen(conversationId,Core.getPreference().getString(SharedPreferencesKeys.UUID,"null"));

                            }
                        });
                    }
                }
            });


            Glide.with(MessagePageActivity.this).load(userdata.getString("profilePic")).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profile);

        }else{
            ReUsableFunctions.ShowToast("Something went wrong");
            finish();
        }

        mainXml.msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    mainXml.cameraBtn.setVisibility(View.GONE);
                    mainXml.addonsSection.setVisibility(View.GONE);
                    mainXml.sendBtn.setVisibility(View.VISIBLE);


                }else{
                    mainXml.sendBtn.setVisibility(TextView.GONE);
                    mainXml.addonsSection.setVisibility(View.VISIBLE);
                    mainXml.cameraBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        mainXml.sendBtn.setOnClickListener(
                v->{
                    String msg=mainXml.msgEditText.getText().toString().trim();
                    if(!msg.isEmpty()){
                        try {
                            Core.sendCtoS(uuid,msg);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        mainXml.msgEditText.setText("");
                    }
                }
        );




    }

    private void setUpRecyclerView(){
        messageAdapter=new MessageAdapter(MessagePageActivity.this,msgList,userdata.getString("profilePic"));
        mainXml.RecyclerView.setLayoutManager(new LinearLayoutManager(MessagePageActivity.this,LinearLayoutManager.VERTICAL,false));
        mainXml.RecyclerView.setAdapter(messageAdapter);
    }
}