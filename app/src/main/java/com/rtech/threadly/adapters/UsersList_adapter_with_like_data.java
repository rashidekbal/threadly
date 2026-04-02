package com.rtech.threadly.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.models.PostLiked_UserModel;

import java.util.ArrayList;

public class UsersList_adapter_with_like_data extends RecyclerView.Adapter<UsersList_adapter_with_like_data.viewHolder> {
Context context;
ArrayList<PostLiked_UserModel> list;

    public UsersList_adapter_with_like_data(Context context, ArrayList<PostLiked_UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userscard_horizontal_with_heart_symbol,parent,false);

        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getProfilePic()).thumbnail(0.1f).placeholder(R.drawable.blank_profile).circleCrop().into(holder.profile);
        holder.userid.setText(list.get(position).getUserId());
        holder.username.setText(list.get(position).getUsername());
        if(list.get(position).isLiked()){
            holder.likeImage.setVisibility(VISIBLE);
        }else{
            holder.likeImage.setVisibility(GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile,likeImage;
        TextView userid,username;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.profile=itemView.findViewById(R.id.userProfile_img);
            this.userid=itemView.findViewById(R.id.user_id_text);
            this.username=itemView.findViewById(R.id.username_text);
            this.likeImage=itemView.findViewById(R.id.likeImage);

        }
    }
}
