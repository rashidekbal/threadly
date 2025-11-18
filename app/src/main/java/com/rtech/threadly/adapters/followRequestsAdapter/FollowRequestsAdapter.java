package com.rtech.threadly.adapters.followRequestsAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.FollowRequestModel;
import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.network_managers.FollowManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FollowRequestsAdapter extends RecyclerView.Adapter<FollowRequestsAdapter.viewHolder> {
    Context context;
    ArrayList<FollowRequestModel> dataList;
    ExecutorService executor=Executors.newSingleThreadExecutor();

    public FollowRequestsAdapter(Context context, ArrayList<FollowRequestModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.follow_request_card,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int rawPosition) {
        int position=holder.getLayoutPosition();
        Glide.with(context).load(dataList.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.profilePic);
        holder.userIdTextView.setText(dataList.get(position).getUserId());
        if(dataList.get(position).isActionTaken()){
            holder.actionTakenTextView.setVisibility( View.VISIBLE);
            holder.actionTakenTextView.setText(dataList.get(position).getActionText());
            holder.approveBtn.setVisibility(View.GONE);
            holder.rejectBtn.setVisibility(View.GONE);
        }else{
            holder.approveBtn.setVisibility(View.VISIBLE);
            holder.rejectBtn.setVisibility(View.VISIBLE);
            holder.actionTakenTextView.setVisibility(View.GONE);
        }

        holder.rejectBtn.setOnClickListener(v->{
            executor.execute(()->{
                DataBase.getInstance().notificationDao().markFollowApprovalStatus(true,Constants.FOLLOW_REQUEST_NOTIFICATION.toString() ,dataList.get(position).getUserId());
            });

            dataList.get(position).setActionText("rejected");
            dataList.get(position).setActionTaken(true);
            holder.actionTakenTextView.setText(dataList.get(position).getActionText());
            holder.rejectBtn.setVisibility(View.GONE);
            holder.approveBtn.setVisibility(View.GONE);
            FollowManager.rejectFollowRequest(dataList.get(position).getUserId(), new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    notifyItemChanged(position);
                }

                @Override
                public void onError(String err) {

                    holder.rejectBtn.setVisibility(View.VISIBLE);
                    holder.approveBtn.setVisibility(View.VISIBLE);
                    dataList.get(position).setActionTaken(false);
                    executor.execute(()->{
                        DataBase.getInstance().notificationDao().markFollowApprovalStatus(false,Constants.FOLLOW_REQUEST_NOTIFICATION.toString() ,dataList.get(position).getUserId());
                    });

                }
            });
        });
        holder.approveBtn.setOnClickListener(v->{
            executor.execute(()->{
                DataBase.getInstance().notificationDao().markFollowApprovalStatus(true,Constants.FOLLOW_REQUEST_NOTIFICATION.toString() ,dataList.get(position).getUserId());
            });

            dataList.get(position).setActionText("approved");
            dataList.get(position).setActionTaken(true);
            holder.actionTakenTextView.setText(dataList.get(position).getActionText());
            holder.rejectBtn.setVisibility(View.GONE);
            holder.approveBtn.setVisibility(View.GONE);
            FollowManager.approveFollowRequest(dataList.get(position).getUserId(), new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
 notifyItemChanged(position);
                }

                @Override
                public void onError(String err) {
                    holder.rejectBtn.setVisibility(View.VISIBLE);
                    holder.approveBtn.setVisibility(View.VISIBLE);
                   dataList.get(position).setActionTaken(false);
                   executor.execute(()->{
                       DataBase.getInstance().notificationDao().markFollowApprovalStatus(false,Constants.FOLLOW_REQUEST_NOTIFICATION.toString() ,dataList.get(position).getUserId());
                   });


                }
            });
        });





    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userIdTextView,actionTakenTextView;
        AppCompatButton approveBtn,rejectBtn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic=itemView.findViewById(R.id.User_profile);
            userIdTextView=itemView.findViewById(R.id.userId_text);
            approveBtn=itemView.findViewById(R.id.approveBtn);
            rejectBtn=itemView.findViewById(R.id.rejectBtn);
            actionTakenTextView=itemView.findViewById(R.id.actionTakenTextView);

        }
    }
}
