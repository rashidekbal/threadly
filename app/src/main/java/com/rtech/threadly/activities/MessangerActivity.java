package com.rtech.threadly.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.RoomDb.schemas.MessageScema;
import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.adapters.messanger.HistoryListAdapter;
import com.rtech.threadly.adapters.messanger.NewMsgListAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityMessangerBinding;
import com.rtech.threadly.fragments.UsersListFragment;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessaageAbleUsersViewModel;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import io.socket.emitter.Emitter;

public class MessangerActivity extends AppCompatActivity {
    ActivityMessangerBinding mainXml;
    SharedPreferences loginInfo;
    MessaageAbleUsersViewModel usersListViewModel;
    ArrayList<HistorySchema> historylist;
    HistoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainXml=ActivityMessangerBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        historylist = new ArrayList<>();

        adapter = new HistoryListAdapter(MessangerActivity.this, model -> {
            Bundle data=new Bundle();
            data.putString("userid",model.getUserId());
            data.putString("username",model.getUsername());
            data.putString("profilePic",model.getProfilePic());
            data.putString("uuid",model.getUuid());
            Intent msgPage=new Intent(MessangerActivity.this,MessagePageActivity.class);
            msgPage.putExtras(data);
            startActivity(msgPage);
        }, historylist);
        usersListViewModel=new ViewModelProvider(MessangerActivity.this).get(MessaageAbleUsersViewModel.class);
        loginInfo=Core.getPreference();
        mainXml.myID.setText(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"));
        setOnclickListeners();
        fetchHistory();
        startListeningMessages();
        setUpRecyclerView();





// preload messageAbleUsers list;
        usersListViewModel.getUsersList().observe( MessangerActivity.this, new Observer<ArrayList<UsersModel>>() {
            @Override
            public void onChanged(ArrayList<UsersModel> usersModels) {

            }
        });
    }


    private void startListeningMessages() {
        SocketManager.getInstance().getSocket().on("StoC", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object =(JSONObject) args[0];
                String ConversationId=object.optString("uuid")+loginInfo.getString(SharedPreferencesKeys.UUID, "null");
                String uuid=object.optString("uuid");
                String username=object.optString("username");
                String userid=object.optString("userid");
                String profile=object.optString("profile");
                String latestMsg=object.optString("msg");
                Executors.newSingleThreadExecutor().execute(() -> {
                    HistorySchema History= DataBase.getInstance().historyOperator().getHistory(ConversationId);
                    if(History==null){
                        DataBase.getInstance().historyOperator().insertHistory(new HistorySchema(ConversationId,username,userid,profile,uuid,latestMsg));

                    }
                    fetchHistory();
                });







            }
        });
    }

    private void setOnclickListeners() {
        mainXml.newMsgBtn.setOnClickListener(v->{

            mainXml.usersListFrameLayout.setVisibility(View.VISIBLE);
            mainXml.newMsgBtn.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.usersListFrameLayout,new UsersListFragment(new OnDestroyFragmentCallback() {
                @Override
                public void onDestroy() {
                    mainXml.newMsgBtn.setVisibility(View.VISIBLE);
                    mainXml.usersListFrameLayout.setVisibility(View.GONE);
                }
            })).addToBackStack(null).commit();
        });
        mainXml.backBtn.setOnClickListener(v->{
            finish();
        });

    }

    private void fetchHistory() {

        Executors.newSingleThreadExecutor().execute(() -> {
            HistorySchema[] templist = DataBase.getInstance().historyOperator().getAllHistory();

            if (templist.length > 0) {
               for(int i=0;i<templist.length;i++){
                   historylist.add(templist[i]);
               }
                runOnUiThread(() -> adapter.notifyDataSetChanged());

            }
        });
    }


    private void setUpRecyclerView() {
        mainXml.UsersListRecyclerView.setLayoutManager(new LinearLayoutManager(MessangerActivity.this,LinearLayoutManager.VERTICAL,false));
        mainXml.UsersListRecyclerView.setAdapter(adapter);

    }



}