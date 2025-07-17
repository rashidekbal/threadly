package com.rtech.threadly.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.rtech.threadly.activities.UserProfileActivity;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.managers.FollowManager;
import com.rtech.threadly.models.Profile_Model_minimal;

import java.util.ArrayList;

public class FollowerFollowing_UserList_adapter extends RecyclerView.Adapter<FollowerFollowing_UserList_adapter.viewHolder> {
    private Context context;
    ArrayList<Profile_Model_minimal> dataList;
    SharedPreferences loginInfo;
    FollowManager followManager;
    public FollowerFollowing_UserList_adapter(Context c,ArrayList<Profile_Model_minimal> dataList) {
        this.dataList= dataList;
        this.context=c;
        this.followManager=new FollowManager();
        this.loginInfo= Core.getPreference();
    }
    private String getMyUserId(){
        return loginInfo.getString(SharedPreferencesKeys.USER_ID,"null");

    }
    @NonNull
    @Override
    public FollowerFollowing_UserList_adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.userscard_horizontal, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerFollowing_UserList_adapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).profilepic).error(R.drawable.blank_profile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.userProfile_img);
        holder.user_id_text.setText(dataList.get(position).userid);
        holder.username_text.setText(dataList.get(position).username);
        if(!dataList.get(position).userid.equals(getMyUserId())){
        if(dataList.get(position).isfollowedBy){
            holder.follow_btn.setVisibility(View.GONE);
            holder.unfollow_btn.setVisibility(View.VISIBLE);
        }else{
            holder.follow_btn.setVisibility(View.VISIBLE);
            holder.unfollow_btn.setVisibility(View.GONE);
        }}else{
            holder.follow_btn.setVisibility(View.GONE);
            holder.unfollow_btn.setVisibility(View.GONE);
        }

//        on follow request listener
        holder.follow_btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                holder.follow_btn.setEnabled(false);
                holder.unfollow_btn.setEnabled(false);
                holder.follow_btn.setVisibility(View.GONE);
                holder.unfollow_btn.setVisibility(View.VISIBLE);
                followManager.follow(dataList.get(position).userid, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.unfollow_btn.setEnabled(true);
                        dataList.get(position).isfollowedBy=true;



                    }

                    @Override
                    public void onError(String err) {
                        holder.follow_btn.setEnabled(true);
                        holder.unfollow_btn.setEnabled(false);
                        holder.follow_btn.setVisibility(View.VISIBLE);
                        holder.unfollow_btn.setVisibility(View.GONE);

                    }
                });
            }
        });
//        unfollow logic
        holder.unfollow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.follow_btn.setEnabled(false);
                holder.unfollow_btn.setEnabled(false);
                holder.follow_btn.setVisibility(View.VISIBLE);
                holder.unfollow_btn.setVisibility(View.GONE);
                followManager.unfollow(dataList.get(position).userid, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.follow_btn.setEnabled(true);
                        dataList.get(position).isfollowedBy=false;

                    }

                    @Override
                    public void onError(String err) {
                        holder.follow_btn.setEnabled(false);
                        holder.unfollow_btn.setEnabled(true);
                        holder.follow_btn.setVisibility(View.GONE);
                        holder.unfollow_btn.setVisibility(View.VISIBLE);


                    }
                });
            }
        });
        holder.userProfile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserProfileActivity.class);
                intent.putExtra("userid",dataList.get(position).userid);
                context.startActivity(intent);
            }
        });
        holder.user_id_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserProfileActivity.class);
                intent.putExtra("userid",dataList.get(position).userid);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView userProfile_img,options_btn;
        TextView user_id_text,username_text;
        AppCompatButton  follow_btn,unfollow_btn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile_img=itemView.findViewById(R.id.userProfile_img);
            user_id_text=itemView.findViewById(R.id.user_id_text);
            username_text=itemView.findViewById(R.id.username_text);
            follow_btn=itemView.findViewById(R.id.follow_btn);
            unfollow_btn=itemView.findViewById(R.id.unfollow_btn);

        }
    }
}
