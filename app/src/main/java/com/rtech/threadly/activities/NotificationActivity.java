package com.rtech.threadly.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.adapters.NotificationAdapters.NotificationAdapter;
import com.rtech.threadly.databinding.ActivityNotificationBinding;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.InteractionNotificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding mainXml;
    InteractionNotificationViewModel notificationViewModel;
    List<NotificationSchema> dataList=new ArrayList<>();
    NotificationAdapter notificationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainXml=ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        notificationViewModel=new ViewModelProvider(this).get(InteractionNotificationViewModel.class);
        notificationAdapter=new NotificationAdapter(dataList,this);
        mainXml.NotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mainXml.NotificationRecyclerView.setAdapter(notificationAdapter);
        ReUsableFunctions.MarkAllNotificationRead();


        //---------------------------
        //observer notification change

        notificationViewModel.getInteractionNotification().observe(this, new Observer<List<NotificationSchema>>() {
            @Override
            public void onChanged(List<NotificationSchema> notificationSchemas) {
//                mainXml.NotificationShimmer.setVisibility(View.GONE);
                if(notificationSchemas.isEmpty()){

                }else{

                    dataList.clear();
                    dataList.addAll(notificationSchemas);
                    mainXml.NotificationRecyclerView.setVisibility(View.VISIBLE);
                    notificationAdapter.notifyDataSetChanged();

                }

            }
        });
        //---------------------------
        //backBtn function setting
        mainXml.backBtn.setOnClickListener(v->{
            super.onBackPressed();
        });
    }
}