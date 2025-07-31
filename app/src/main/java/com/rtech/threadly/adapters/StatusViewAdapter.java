package com.rtech.threadly.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.models.StoriesModel;

import java.util.ArrayList;

public class StatusViewAdapter extends RecyclerView.Adapter<StatusViewAdapter.viewHolder> {
    Context context;
    ArrayList<StoriesModel> list;
    public StatusViewAdapter(Context c, ArrayList<StoriesModel> list){
        this.context=c;
        this.list=list;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public StatusViewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.status_card,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewAdapter.viewHolder holder, int position) {
        if(list.get(position).isSeen){
            holder.dpBorder.setBackground(context.getDrawable(R.drawable.circle_grey));
        }else{
            holder.dpBorder.setBackground(context.getDrawable(R.drawable.red_circle));
        }
        Glide.with(context).load(list.get(position).userProfile).placeholder(R.drawable.blank_profile).circleCrop().into(holder.profileImg);

        holder.userid.setText(list.get(position).userid);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView userid;
        ImageView profileImg;
        FrameLayout dpBorder;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg=itemView.findViewById(R.id.profile_img);
            userid=itemView.findViewById(R.id.userid);
            dpBorder=itemView.findViewById(R.id.StoryOuterBorder_color);
        }
    }
}
