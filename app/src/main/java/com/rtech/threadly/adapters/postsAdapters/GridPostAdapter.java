package com.rtech.threadly.adapters.postsAdapters;

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
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.Preview_Post_model;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.utils.CoilUtil;

import java.util.ArrayList;

public class GridPostAdapter extends RecyclerView.Adapter<GridPostAdapter.ViewHolder> {
    Context context;
    ArrayList<Posts_Model> dataList;
    Post_fragmentSetCallback post_fragmentSetCallback;
    public GridPostAdapter(Context c, ArrayList<Posts_Model> data, Post_fragmentSetCallback callback){
        this.dataList=data;
        this.context=c;
        setHasStableIds(true);
        this.post_fragmentSetCallback=callback;

    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getPostId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String previewUrl=dataList.get(position).postUrl.replace(".mp4",".jpg");
        Glide.with(context).load(previewUrl).thumbnail(0.1f).placeholder(R.drawable.post_placeholder).into(holder.imageView);
        holder.imageView.setOnClickListener(v -> post_fragmentSetCallback.openPostFragment(dataList,position));
        holder.viewCount.setText(String.valueOf(dataList.get(position).getViewCount()));


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView viewCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_view);
            viewCount=itemView.findViewById(R.id.viewsCount);

        }
    }
}
