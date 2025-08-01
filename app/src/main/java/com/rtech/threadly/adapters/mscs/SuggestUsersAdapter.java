package com.rtech.threadly.adapters.mscs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.managers.FollowManager;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;

import java.util.ArrayList;

public class SuggestUsersAdapter extends RecyclerView.Adapter<SuggestUsersAdapter.viewHolder>{
    Context context;
    ArrayList<Profile_Model_minimal> list;
    FollowManager followManager;
    public SuggestUsersAdapter(Context context, ArrayList<Profile_Model_minimal> list){
        this.list=list;
        this.context=context;
        this.followManager=new FollowManager();
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.users_card,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.username_text.setText(list.get(position).username);
        Glide.with(context).load(list.get(position).profilepic).placeholder(R.drawable.blank_profile).circleCrop().into(holder.userProfile_img);
        holder.follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.follow_btn.setEnabled(false);
                followManager.follow(list.get(position).userid, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.follow_btn.setEnabled(true);
                        holder.follow_btn.setVisibility(View.GONE);
                        holder.unfollow_btn.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(String err) {
                        Log.d("followError", "onError: ".concat(err));
                        holder.follow_btn.setEnabled(true);

                    }
                });
            }
        });
        holder.unfollow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followManager.unfollow(list.get(position).userid, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.unfollow_btn.setEnabled(true);
                        holder.unfollow_btn.setVisibility(View.GONE);
                        holder.follow_btn.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(String err) {
                        Log.d("followError", "onError: ".concat(err));
                        holder.unfollow_btn.setEnabled(true);

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView removeCard_imageBtn;
        ImageView userProfile_img;
        TextView username_text;
        ImageView common_follower_imageView;
        TextView common_follower_textView;
        androidx.appcompat.widget.AppCompatButton follow_btn;
        androidx.appcompat.widget.AppCompatButton unfollow_btn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            removeCard_imageBtn=itemView.findViewById(R.id.removeCard_imageBtn);
            userProfile_img=itemView.findViewById(R.id.userProfile_img);
            username_text=itemView.findViewById(R.id.username_text);
            common_follower_imageView=itemView.findViewById(R.id.common_follower_imageView);
            common_follower_textView=itemView.findViewById(R.id.common_follower_textView);
            follow_btn=itemView.findViewById(R.id.follow_btn);
            unfollow_btn=itemView.findViewById(R.id.unfollow_btn);

        }
    }
}