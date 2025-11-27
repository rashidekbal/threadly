package com.rtech.threadly.adapters.NotificationAdapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.activities.PostActivity;
import com.rtech.threadly.constants.Constants;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.List;
import java.util.concurrent.Executors;


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<NotificationSchema> dataSource;
    Context context;
    FollowManager followManager;
    int FOLLOW_NOTIFICATION_TYPE=0;
    int POST_LIKE_NOTIFICATION_TYPE=1;
    int COMMENT_LIKE_NOTIFICATION_TYPE=2;
    int FOLLOW_ACCEPTED_NOTIFICATION_TYPE=3;


    public NotificationAdapter(List<NotificationSchema> dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context;
        this.followManager = new FollowManager();
    }

    @Override
    public int getItemViewType(int position) {
        if(dataSource.get(position).getNotificationType().equals(Constants.FOLLOW_NOTIFICATION.toString())){
            return FOLLOW_NOTIFICATION_TYPE;
        }else if(dataSource.get(position).getNotificationType().equals(Constants.POST_LIKE_NOTIFICATION.toString())){
            return POST_LIKE_NOTIFICATION_TYPE;
        }else if(dataSource.get(position).getNotificationType().equals(Constants.FOLLOW_ACCEPTED_NOTIFICATION.toString())){
            return FOLLOW_ACCEPTED_NOTIFICATION_TYPE;
        } else{
            return COMMENT_LIKE_NOTIFICATION_TYPE;
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        if(viewType==FOLLOW_NOTIFICATION_TYPE){
            return new FollowViewHolder(inflater.inflate(R.layout.followed_by_user_card,parent,false));
        } else if (viewType==POST_LIKE_NOTIFICATION_TYPE) {
            return new PostLikeViewHolder(inflater.inflate(R.layout.post_like_card_layout,parent,false));

        }else if(viewType==FOLLOW_ACCEPTED_NOTIFICATION_TYPE){
            return new FollowRequestAcceptedViewHolder(inflater.inflate(R.layout.follow_request_card,parent,false));
        }
        else{
            return new CommentLikeViewHolder(inflater.inflate(R.layout.comment_liked_notification_card,parent,false));


        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewholder, @SuppressLint("RecyclerView") int rawPositon) {
        int position=viewholder.getLayoutPosition();
        if(viewholder instanceof FollowViewHolder){
            //follow view holder
            FollowViewHolder holder=(FollowViewHolder) viewholder;
            Glide.with(context).load(dataSource.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.User_profile);
            holder.userId_text.setText(dataSource.get(position).getUsername()+" started following you");
            holder.User_profile.setOnClickListener(v-> ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
            holder.userId_text.setOnClickListener(v->ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
            if(!dataSource.get(position).isFollowed()){
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setEnabled(true);
                holder.followBtn.setOnClickListener(v->{
                    holder.followBtn.setEnabled(false);
                    holder.followBtn.setVisibility(View.GONE);
                    followManager.follow(dataSource.get(position).getUserId(), new NetworkCallbackInterface() {

                        @Override
                        public void onSuccess() {
                            holder.followBtn.setEnabled(true);
                            Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().notificationDao().markedFollowState(1,dataSource.get(position).getNotificationId()));
                            notifyItemChanged(position);
                            

                        }

                        @Override
                        public void onError(String err) {
                            Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().notificationDao().markedFollowState(0,dataSource.get(position).getNotificationId()));
                            holder.followBtn.setEnabled(true);
                            holder.followBtn.setVisibility(View.VISIBLE);
                            notifyItemChanged(position);

                        }

                    });
                });

            }else{
                holder.followBtn.setVisibility(View.GONE);
            }


        }
        else if(viewholder instanceof PostLikeViewHolder){
            //post like viewHolder
            PostLikeViewHolder holder=(PostLikeViewHolder) viewholder;
            Glide.with(context).load(dataSource.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.User_profile);
            holder.userId_text.setText(dataSource.get(position).getUsername()+" like your post ");
            Glide.with(context).load(dataSource.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.postPreviewImg);
            holder.User_profile.setOnClickListener(v-> ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
            holder.userId_text.setOnClickListener(v-> ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
            holder.postPreviewImg.setOnClickListener(v-> context.startActivity(new Intent(context, PostActivity.class).putExtra("postid",dataSource.get(position).getPostId())));




        }
        else  if(viewholder instanceof FollowRequestAcceptedViewHolder){
            BindFollowRequestAcceptedViewHolder((FollowRequestAcceptedViewHolder) viewholder,position);
        }
        else{
            CommentLikeViewHolder holder=(CommentLikeViewHolder) viewholder;
            Glide.with(context).load(dataSource.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.User_profile);
            holder.userId_text.setText(dataSource.get(position).getUsername()+" like your Comment ");
            Glide.with(context).load(dataSource.get(position).getPostLink()).placeholder(R.drawable.post_placeholder).into(holder.postPreviewImg);
            holder.User_profile.setOnClickListener(v-> ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
            holder.userId_text.setOnClickListener(v-> ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
            holder.postPreviewImg.setOnClickListener(v-> context.startActivity(new Intent(context, PostActivity.class).putExtra("postid",dataSource.get(position).getPostId())));

        }

    }

    private void BindFollowRequestAcceptedViewHolder(@NonNull FollowRequestAcceptedViewHolder holder, int position) {
        Glide.with(context).load(dataSource.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(holder.User_profile);
        holder.User_profile.setOnClickListener(v-> ReUsableFunctions.openProfile(context,dataSource.get(position).getUserId()));
        holder.messageText.setText(dataSource.get(position).getUsername()+context.getString(R.string.follow_request_accepted_text));

    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
    static class PostLikeViewHolder extends RecyclerView.ViewHolder{
ImageView User_profile,postPreviewImg;
TextView userId_text;
        public PostLikeViewHolder(@NonNull View itemView) {
            super(itemView);
            User_profile=itemView.findViewById(R.id.User_profile);
            userId_text=itemView.findViewById(R.id.userId_text);
            postPreviewImg=itemView.findViewById(R.id.postPreviewImg);
        }
    }
    static class FollowViewHolder extends RecyclerView.ViewHolder{
ImageView User_profile;
AppCompatButton followBtn;
TextView userId_text;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            User_profile=itemView.findViewById(R.id.User_profile);
            userId_text=itemView.findViewById(R.id.userId_text);
            followBtn=itemView.findViewById(R.id.followBtn);
        }
    }
    static class CommentLikeViewHolder extends RecyclerView.ViewHolder{
        ImageView User_profile,postPreviewImg;
        TextView userId_text;
        public CommentLikeViewHolder(@NonNull View itemView) {
            super(itemView);
            User_profile=itemView.findViewById(R.id.User_profile);
            userId_text=itemView.findViewById(R.id.userId_text);
            postPreviewImg=itemView.findViewById(R.id.postPreviewImg);
        }
    }

    private static class FollowRequestAcceptedViewHolder extends  RecyclerView.ViewHolder{
        ImageView User_profile;
        AppCompatButton approveBtn;
        TextView messageText;
        public FollowRequestAcceptedViewHolder(@NonNull View itemView) {
            super(itemView);
            User_profile=itemView.findViewById(R.id.User_profile);
            messageText =itemView.findViewById(R.id.userId_text);
            approveBtn=itemView.findViewById(R.id.approveBtn);
            approveBtn.setVisibility(View.GONE);
        }
    }
}