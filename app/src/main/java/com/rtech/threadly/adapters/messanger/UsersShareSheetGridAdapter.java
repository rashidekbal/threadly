package com.rtech.threadly.adapters.messanger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.models.UsersModel;

import java.util.ArrayList;

public class UsersShareSheetGridAdapter extends RecyclerView.Adapter<UsersShareSheetGridAdapter.viewHolder> {
    ArrayList<UsersModel> selectedUsers=new ArrayList<>();
    Context context;
    ArrayList<UsersModel> usersList;
    OnUserSelectedListener userSelectedListener;
    public UsersShareSheetGridAdapter(Context context,ArrayList<UsersModel> usersList,OnUserSelectedListener callback){
        this.context=context;
        this.usersList=usersList;
        this.userSelectedListener=callback;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // reusing the status card as the share user suggestion
        View v= LayoutInflater.from(context).inflate(R.layout.content_share_user_card,parent,false);

        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if(selectedUsers.contains(usersList.get(position))){
            holder.tick_view.setVisibility(View.VISIBLE);
        }else{
            holder.tick_view.setVisibility(View.GONE);

        }
        Glide.with(context).load(usersList.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.profile);
        holder.userName.setText(usersList.get(position).getUsername());
        holder.userBtn.setOnClickListener(v->{
            if(selectedUsers.contains(usersList.get(position))){
                userSelectedListener.onSelect(usersList.get(position));
                holder.tick_view.setVisibility(View.GONE);
                    selectedUsers.remove(usersList.get(position));
            }
            else{
                userSelectedListener.onSelect(usersList.get(position));
                selectedUsers.add(usersList.get(position));
                holder.tick_view.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        ImageView profile,tick_view;
        TextView userName;
        LinearLayout userBtn;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
        userName=itemView.findViewById(R.id.userid);
        profile=itemView.findViewById(R.id.profile_img);
        userBtn=itemView.findViewById(R.id.storyLayout);
        tick_view=itemView.findViewById(R.id.tick_view);
    }
}
}

