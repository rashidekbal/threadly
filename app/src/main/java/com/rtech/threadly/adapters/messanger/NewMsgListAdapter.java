package com.rtech.threadly.adapters.messanger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.models.UsersModel;

import java.util.ArrayList;

public class NewMsgListAdapter extends RecyclerView.Adapter<NewMsgListAdapter.viewHolder> {
    Context context;
    ArrayList<UsersModel> list;
    OnUserSelectedListener onUserSelectedListener;

    public NewMsgListAdapter(Context context, OnUserSelectedListener onUserSelectedListener, ArrayList<UsersModel> list) {
        this.context = context;
        this.onUserSelectedListener = onUserSelectedListener;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userscard_horizontal,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.parent.setEnabled(true);
        holder.user_id_text.setText(list.get(position).getUserId());
        holder.username_text.setText(list.get(position).getUsername());
        holder.follow_btn.setVisibility(View.GONE);
        holder.options_btn.setVisibility(View.GONE);
        Glide.with(context).load(list.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop()
                .into(holder.userProfile_img);
        holder.parent.setOnClickListener(v->{
            holder.parent.setEnabled(false);
            onUserSelectedListener.onSelect(list.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parent;
ImageView userProfile_img,options_btn;
TextView user_id_text,username_text;
AppCompatButton follow_btn;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile_img=itemView.findViewById(R.id.userProfile_img);
            options_btn=itemView.findViewById(R.id.options_btn);
            user_id_text=itemView.findViewById(R.id.user_id_text);
            username_text=itemView.findViewById(R.id.username_text);
            follow_btn=itemView.findViewById(R.id.follow_btn);
            parent =itemView.findViewById(R.id.parent);
        }
    }
}
