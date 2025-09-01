package com.rtech.threadly.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.HistorySchema;
import com.rtech.threadly.adapters.messanger.HistoryListAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.ActivityMessangerBinding;
import com.rtech.threadly.fragments.UsersListFragment;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.viewmodels.UsersMessageHistoryProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessangerActivity extends AppCompatActivity {
    ActivityMessangerBinding mainXml;
    SharedPreferences loginInfo;
    ArrayList<HistorySchema> historylist;
    HistoryListAdapter adapter;
    UsersMessageHistoryProfileViewModel historyCardViewModel;

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
        historyCardViewModel=new ViewModelProvider(MessangerActivity.this).get(UsersMessageHistoryProfileViewModel.class);
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

        loginInfo=Core.getPreference();

        mainXml.myID.setText(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"));
        setOnclickListeners();
        setUpRecyclerView();
        historyCardViewModel.getHistory().observe(MessangerActivity.this, new Observer<List<HistorySchema>>() {
            @Override
            public void onChanged(List<HistorySchema> historySchemas) {
                historylist.clear();
                historylist.addAll(historySchemas);
                adapter.notifyDataSetChanged();
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
    private void setUpRecyclerView() {
        mainXml.UsersListRecyclerView.setLayoutManager(new LinearLayoutManager(MessangerActivity.this,LinearLayoutManager.VERTICAL,false));
        mainXml.UsersListRecyclerView.setAdapter(adapter);
    }
}