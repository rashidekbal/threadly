package com.rtech.gpgram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.gpgram.R;

import java.util.ArrayList;

public class StatusViewAdapter extends RecyclerView.Adapter<StatusViewAdapter.viewHolder> {
    Context context;
    ArrayList<String> list;
    public StatusViewAdapter(Context c, ArrayList<String> list){
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
        Glide.with(context).load(R.drawable.image_test).circleCrop().into(holder.profileImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg=itemView.findViewById(R.id.profile_img);
        }
    }
}
