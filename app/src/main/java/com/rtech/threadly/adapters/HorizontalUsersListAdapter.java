package com.rtech.threadly.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;

public class HorizontalUsersListAdapter extends RecyclerView.Adapter<HorizontalUsersListAdapter.viewHolder> {
    Context context;
    ArrayList<UsersModel> list;
    public HorizontalUsersListAdapter(Context context,ArrayList<UsersModel>list){
        this.context=context;
        this.list=list;



    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getUserId().hashCode();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userscard_horizontal,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.rightSide.setVisibility(GONE);
        Glide.with(context).load(list.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.profile);
        holder.userid.setText(list.get(position).getUserId());
        holder.username.setText(list.get(position).getUsername());
        holder.itemView.setOnClickListener(v->{
            ReUsableFunctions.openProfile(context,list.get(position).getUserId());
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView userid,username;
        RelativeLayout rightSide;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.rightSide=itemView.findViewById(R.id.right_side);
            this.profile=itemView.findViewById(R.id.userProfile_img);
            this.userid=itemView.findViewById(R.id.user_id_text);
            this.username=itemView.findViewById(R.id.username_text);

        }
    }
}
