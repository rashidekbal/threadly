package com.rtech.threadly.adapters.storiesAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.managers.LikeManager;
import com.rtech.threadly.models.StoryMediaModel;

import java.util.ArrayList;

public class StoriesViewpagerAdapter extends RecyclerView.Adapter<StoriesViewpagerAdapter.viewHolder> {
    ArrayList<StoryMediaModel> storiesData;
    Context context;
    LikeManager likeManager;
    boolean isLiked;
    public StoriesViewpagerAdapter(ArrayList<StoryMediaModel> storiesData,Context context) {
        this.context=context;
        this.storiesData = storiesData;
        this.likeManager=new LikeManager();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.story_view_layout,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onViewRecycled(@NonNull viewHolder holder) {
        super.onViewRecycled(holder);
        holder.playerView.setPlayer(null);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        isLiked=storiesData.get(position).isLiked();
        if(!storiesData.get(position).isVideo()){
            //if the post is image
            holder.post_image_view.setVisibility(View.VISIBLE);
            holder.playerView.setVisibility(View.GONE);
            Glide.with(context).load(storiesData.get(position).getStoryUrl()).placeholder(R.drawable.post_placeholder).into(holder.post_image_view);

        }else{
            holder.post_image_view.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.VISIBLE);
        }
      if(storiesData.get(position).isLiked()){
          holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
      }else {
          holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
      }
      setOnclickListeners(holder,position);


    }
//onBind end here
    private void setOnclickListeners(viewHolder holder, int position) {
        holder.like_btn_image.setOnClickListener(v->{
            if(isLiked){
                //if unlike
                holder.like_btn_image.setEnabled(false);
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
                storiesData.get(position).setLiked(false);
                isLiked=false;
                likeManager.UnLikeStory(storiesData.get(position).getStoryId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);


                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setEnabled(true);
                        holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                        storiesData.get(position).setLiked(true);
                        isLiked=true;



                    }
                });


            }
            else{
                //like

                holder.like_btn_image.setEnabled(false);
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                storiesData.get(position).setLiked(true);
                isLiked=true;
                likeManager.LikeStory(storiesData.get(position).getStoryId(), new NetworkCallbackInterface() {

                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);

                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setEnabled(true);
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
                        storiesData.get(position).setLiked(false);
                        isLiked=false;

                    }
                });
            }


        });



//            //unlike if already liked
//            holder.like_btn_image.setOnClickListener(v->{
//
//                if(storiesData.get(position).isLiked()){
//
//
//            });



    }

    @Override
    public int getItemCount() {
        return storiesData.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView post_image_view,share_btn,like_btn_image;
       public PlayerView playerView;
        ProgressBar progressBar;
        View previous_btn,next_btn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            post_image_view=itemView.findViewById(R.id.post_image_view);
            playerView=itemView.findViewById(R.id.videoPlayer_view);
            progressBar=itemView.findViewById(R.id.progressBar);
            share_btn=itemView.findViewById(R.id.share_btn);
            like_btn_image=itemView.findViewById(R.id.like_btn_image);
            previous_btn=itemView.findViewById(R.id.previous_btn);
            next_btn=itemView.findViewById(R.id.next_btn);
        }
    }
}
