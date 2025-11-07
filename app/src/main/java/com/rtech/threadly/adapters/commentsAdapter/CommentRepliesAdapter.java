package com.rtech.threadly.adapters.commentsAdapter;

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
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;

public class CommentRepliesAdapter extends RecyclerView.Adapter<CommentRepliesAdapter.viewHolder> {
    Context context;
    ArrayList<Posts_Comments_Model> commentsModels;
    LikeManager likeManager;
    public CommentRepliesAdapter(Context context, ArrayList<Posts_Comments_Model> commentsModels) {
        this.context = context;
        this.commentsModels = commentsModels;
        this.likeManager=new LikeManager();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(context).inflate(R.layout.comment_card,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Glide.with(context).load(commentsModels.get(position).getUserDpUrl()).circleCrop().placeholder(R.drawable.blank_profile).into(holder.userProfile_img);
        holder.username_text.setText(commentsModels.get(position).getUserId());
        holder.comment_text.setText(commentsModels.get(position).getComment());
        if(commentsModels.get(position).isLiked()){
            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
        }else {
            holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
        }
        holder.likes_count_text.setText(Integer.toString(commentsModels.get(position).getLikesCount()));
//        open userProfile on click of profile pic
        holder.userProfile_img.setOnClickListener(v -> ReUsableFunctions.openProfile(context,commentsModels.get(position).getUserId()));
        holder.username_text.setOnClickListener(v -> ReUsableFunctions.openProfile(context,commentsModels.get(position).getUserId()));
        //like action
        /// like a comment
        holder.like_btn_image.setOnClickListener(v -> {
           boolean isLikedByMe=commentsModels.get(position).isLiked();
            holder.like_btn_image.setEnabled(false);
            /// dislike this comment
            if(isLikedByMe) {
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
                Posts_Comments_Model object = commentsModels.get(position);
                commentsModels.get(position).setLiked(false);
                commentsModels.get(position).setLikesCount(commentsModels.get(position).getLikesCount()-1);
                notifyItemChanged(position);
                likeManager.UnLikeAComment(commentsModels.get(position).getCommentId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);


                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                        commentsModels.get(position).setLiked(true);
                        commentsModels.get(position).setLikesCount(commentsModels.get(position).getLikesCount()+1);
                        notifyItemChanged(position);
                        holder.like_btn_image.setEnabled(true);


                    }
                });
            }
            //like this comment
            else{
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                commentsModels.get(position).setLiked(true);
                commentsModels.get(position).setLikesCount(commentsModels.get(position).getLikesCount()+1);
                notifyItemChanged(position);
                likeManager.LikeAComment(commentsModels.get(position).getCommentId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);
                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
                        commentsModels.get(position).setLiked(false);
                        commentsModels.get(position).setLikesCount(commentsModels.get(position).getLikesCount()-1);
                        notifyItemChanged(position);
                        holder.like_btn_image.setEnabled(true);

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsModels.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView userProfile_img,like_btn_image;
        TextView username_text,comment_text,likes_count_text;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile_img=itemView.findViewById(R.id.userProfile_img);
            username_text=itemView.findViewById(R.id.username_text);
            like_btn_image=itemView.findViewById(R.id.like_btn_image);
            comment_text=itemView.findViewById(R.id.comment_text);
            likes_count_text=itemView.findViewById(R.id.likes_count_text);
        }
    }
}
