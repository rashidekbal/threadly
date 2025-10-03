package com.rtech.threadly.adapters.NotificationAdapters;

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
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.constants.Constants;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<NotificationSchema> dataSource;
    Context context;
    int POST_LIKE_TYPE=0;
    int COMMENT_LIKE_TYPE=1;
    int FOLLOW_TYPE=2;
    public NotificationAdapter(List<NotificationSchema> dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        String  type=dataSource.get(position).getNotificationType();
        if(type.equals(Constants.FOLLOW_NOTIFICATION.toString())){
            return 2;
        } else if (type.equals(Constants.COMMENT_LIKE_NOTIFICATION.toString())) {
            return 1;

        } else {
            return 0;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view;
        if(viewType==POST_LIKE_TYPE){
            view=layoutInflater.inflate(R.layout.post_like_card_layout,parent,false);
            return  new PostLikeNotificationViewHolder(view);
        } else if (viewType==COMMENT_LIKE_TYPE) {
            view=layoutInflater.inflate(R.layout.suggest_section_card,parent,false);
            return new CommentLikeNotificationViewHolder(view);

        }else{
           view=layoutInflater.inflate(R.layout.reel_layout,parent,false);
           return new CommentLikeNotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostLikeNotificationViewHolder){
            //post like notification
            Glide.with(context).load(dataSource.get(position).getProfilePic()).placeholder(R.drawable.blank_profile).circleCrop().into(((PostLikeNotificationViewHolder)holder).profile);
            ((PostLikeNotificationViewHolder)holder).userid.setText(dataSource.get(position).getUserId());
            Glide.with(context).load(dataSource.get(position).getPostLink()).placeholder(R.drawable.edged_grey_box).into(((PostLikeNotificationViewHolder)holder).preview);
        } else if (holder instanceof CommentLikeNotificationViewHolder) {
            //comment like notification

        }else{
            //follow notification

        }

    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
    static class FollowNotificationViewHolder extends RecyclerView.ViewHolder {
        public FollowNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    static class PostLikeNotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView profile,preview;
        TextView userid;
        public PostLikeNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.User_profile);
            preview=itemView.findViewById(R.id.postPreviewImg);
            userid=itemView.findViewById(R.id.userId_text);
        }
    }
    static  class CommentLikeNotificationViewHolder extends RecyclerView.ViewHolder{

        public CommentLikeNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
