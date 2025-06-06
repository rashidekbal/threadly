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
import com.rtech.gpgram.structures.SearchpagePost_data_structure_base;

import java.util.ArrayList;

public class SearchPagePostAdapter extends RecyclerView.Adapter<SearchPagePostAdapter.ViewHolder> {
    Context context;
    ArrayList<SearchpagePost_data_structure_base> dataList;
    public SearchPagePostAdapter(Context c, ArrayList<SearchpagePost_data_structure_base> data){
        this.dataList=data;
        this.context=c;
        setHasStableIds(true);

    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_image_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).image_url).placeholder(R.drawable.post_placeholder).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_view);

        }
    }
}
