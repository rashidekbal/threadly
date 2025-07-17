package com.rtech.threadly.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.uploadProfileSelectCallbackInterface;

import java.util.ArrayList;

public class uploadProfileAdapter extends RecyclerView.Adapter<uploadProfileAdapter.viewHolder> {
    private final ArrayList<Uri> list;
    private final Context context;
    private uploadProfileSelectCallbackInterface callbackInterface;
    public uploadProfileAdapter(ArrayList<Uri> list, Context context, uploadProfileSelectCallbackInterface callback){
        this.list=list;
        this.context=context;
        this.callbackInterface=callback;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.media_picker_grid_view_sample_post,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackInterface.itemPicked(list.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public viewHolder(View itemView){
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
