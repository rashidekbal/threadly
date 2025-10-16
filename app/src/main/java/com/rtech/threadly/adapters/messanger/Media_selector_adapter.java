package com.rtech.threadly.adapters.messanger;

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
import com.rtech.threadly.interfaces.general_ui_callbacks.OnMediaClicked;
import com.rtech.threadly.models.MediaModel;

import java.util.ArrayList;

public class Media_selector_adapter extends RecyclerView.Adapter<Media_selector_adapter.viewHolder> {
    Context context;
    ArrayList<MediaModel> mediaList;
    OnMediaClicked onclickCallback;
         public Media_selector_adapter(Context c, ArrayList<MediaModel> mediaModels, OnMediaClicked mediaClickCallback){
          this.context=c;
          this.onclickCallback=mediaClickCallback;
          this.mediaList=mediaModels;
         }

    @NonNull
    @Override
    public Media_selector_adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             View v= LayoutInflater.from(context).inflate(R.layout.media_picker_grid_view_sample_post,parent,false);
        return new viewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull Media_selector_adapter.viewHolder holder, int position) {
             if(mediaList.get(position).isVideo){
                 holder.durationText.setVisibility(View.VISIBLE);
                 holder.durationText.setText(Integer.toString(mediaList.get(position).duration));
                 Glide.with(context).load(mediaList.get(position).uri).placeholder(R.drawable.post_placeholder).into(holder.previewImage);

             }else{
                 Glide.with(context).load(mediaList.get(position).uri).placeholder(R.drawable.post_placeholder).into(holder.previewImage);
                 holder.durationText.setVisibility(View.GONE);


             }
             holder.previewImage.setOnClickListener(v->{
                 onclickCallback.MediaClicked(mediaList.get(position));
             });

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
             ImageView previewImage;
             TextView durationText;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            previewImage=itemView.findViewById(R.id.imageView);
            durationText=itemView.findViewById(R.id.duration);

        }
    }
}
