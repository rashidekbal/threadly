package com.rtech.threadly.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.AddPostMainFragmentOptionsClickInterface;
import com.rtech.threadly.models.MediaModel;

import java.util.ArrayList;

public class AddPostShowMediaAdapter extends RecyclerView.Adapter<AddPostShowMediaAdapter.viewHolder> {
    ArrayList<MediaModel> medialist;
    Context context;
    AddPostMainFragmentOptionsClickInterface callback;
    public AddPostShowMediaAdapter(Context context, ArrayList<MediaModel> mediaList,AddPostMainFragmentOptionsClickInterface callback) {
        this.medialist=mediaList;
        this.context=context;
        this.callback=callback;
    }

    @NonNull
    @Override
    public AddPostShowMediaAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.media_picker_grid_view_sample_post,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddPostShowMediaAdapter.viewHolder holder, int position) {
        if(medialist.get(position).isCameraIntent){
            holder.overlay.setVisibility(View.GONE);
            holder.image_view_layout.setVisibility(View.GONE);
            holder.openCameraButton.setVisibility(View.VISIBLE);
            holder.openCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.openCamera();
                }
            });

        }
        else{
            holder.openCameraButton.setVisibility(View.GONE);
        MediaModel mediaModel=medialist.get(position);
        holder.overlay.setVisibility(View.GONE);
        Glide.with(context).load(mediaModel.uri).thumbnail(0.1f).into(holder.imageView);
        if(mediaModel.isVideo){
            holder.duration.setVisibility(View.VISIBLE);
            holder.duration.setText(Integer.toString(mediaModel.duration)+":00");

        }else{
            holder.duration.setVisibility(View.GONE);

        }


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.itemPicked(mediaModel.uri.toString(),mediaModel.isVideo ? "video" : "image");
            }
        });

    }}

    @Override
    public int getItemCount() {
        return medialist.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,openCameraButton;
        TextView duration;
        LinearLayout overlay;
        RelativeLayout image_view_layout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            openCameraButton=itemView.findViewById(R.id.openCameraButton);
            imageView=itemView.findViewById(R.id.imageView);
            duration=itemView.findViewById(R.id.duration);
            overlay=itemView.findViewById(R.id.overlay);
            image_view_layout=itemView.findViewById(R.id.image_view_layout);

        }
    }
}
