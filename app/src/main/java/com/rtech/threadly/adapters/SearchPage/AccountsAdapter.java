package com.rtech.threadly.adapters.SearchPage;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.viewHolder> {
    ArrayList<UsersModel> usersList;
    Context context;

    public AccountsAdapter(ArrayList<UsersModel> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.user_search_result_card,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsAdapter.viewHolder holder, int rawPosition) {
       int position=holder.getLayoutPosition();
       BindViewHolder(holder,position);
    }

    private void BindViewHolder(viewHolder holder, int position) {
        Glide.with(context).load(usersList.get(position).getProfilePic()).circleCrop().placeholder(R.drawable.blank_profile).into(holder.profileImg);
        holder.useridText.setText(usersList.get(position).getUserId());
        holder.usernameText.setText(usersList.get(position).getUsername());
        holder.itemView.setOnClickListener(v -> {
            ReUsableFunctions.openProfile(context,usersList.get(position).getUserId());
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg;
        TextView useridText,usernameText;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg=itemView.findViewById(R.id.userProfile_img);
            useridText=itemView.findViewById(R.id.user_id_text);
            usernameText=itemView.findViewById(R.id.username_text);
        }
    }
}
