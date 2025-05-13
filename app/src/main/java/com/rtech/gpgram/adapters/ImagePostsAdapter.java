package com.rtech.gpgram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rtech.gpgram.R;

import java.util.ArrayList;

public class ImagePostsAdapter extends RecyclerView.Adapter<ImagePostsAdapter.viewHolder> {
    Context context;
    ArrayList<String > list;
    public ImagePostsAdapter(Context c, ArrayList<String> list){
        this.context=c;
        this.list=list;
    }
    @NonNull
    @Override
    public ImagePostsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.pic_post_card,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePostsAdapter.viewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
