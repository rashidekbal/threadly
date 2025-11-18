package com.rtech.threadly.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.adapters.NotificationAdapters.NotificationAdapter;
import com.rtech.threadly.databinding.ActivityNotificationBinding;
import com.rtech.threadly.fragments.notification.FollowRequestsFragment;
import com.rtech.threadly.utils.PreferenceUtil;
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

        notificationViewModel.getInteractionNotification().observe(this, notificationSchemas -> {
//                mainXml.NotificationShimmer.setVisibility(View.GONE);
            if(!notificationSchemas.isEmpty()){
                dataList.clear();
                dataList.addAll(notificationSchemas);
                mainXml.NotificationRecyclerView.setVisibility(View.VISIBLE);
                notificationAdapter.notifyDataSetChanged();

            }

        });
        setUpPendingFollowView();

        //---------------------------
        //backBtn function setting
        mainXml.backBtn.setOnClickListener(v-> super.onBackPressed());
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount()==0){
                    mainXml.frameLayout.setVisibility(View.GONE);
                    return;
                }
                mainXml.frameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpPendingFollowView() {

        if(PreferenceUtil.isPrivate()){
            mainXml.followRequestsLayout.setVisibility(View.VISIBLE);

            mainXml.followRequestsLayout.setOnClickListener(v->{
                addFragment(new FollowRequestsFragment());
            });




            notificationViewModel.getUnInteractedRequestCount().observe(this,count->{
               if(count==null) {
                   mainXml.requestsCountText.setVisibility(View.GONE);
                   return;
               }
                setPendingRequestCount(count);
            });

        }else{
            mainXml.frameLayout.setVisibility(View.GONE);
            mainXml.followRequestsLayout.setVisibility(View.GONE);
        }
    }

    private void setPendingRequestCount(Integer count) {
        if(count<1){mainXml.requestsCountText.setVisibility(View.GONE);return;}
            mainXml.requestsCountText.setVisibility(View.VISIBLE);
            mainXml.requestsCountText.setText(count>4?count+" +":Integer.toString(count));
    }
    private  void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(mainXml.frameLayout.getId(),fragment).addToBackStack(null).commit();
    }


}