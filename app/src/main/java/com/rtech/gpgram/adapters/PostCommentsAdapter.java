package com.rtech.gpgram.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.R;
import com.rtech.gpgram.structures.PostCommentsDataStructure;

import org.json.JSONObject;

import java.util.ArrayList;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.viewHolder> {
    String url= BuildConfig.BASE_URL;
    boolean isLikedByMe;
    Context context;
    ArrayList<PostCommentsDataStructure> dataList;
    SharedPreferences loginInfo;

    public  PostCommentsAdapter (Context c,ArrayList<PostCommentsDataStructure> dataList){
        this.context=c;
        this.dataList=dataList;

    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_card,parent,false);
        loginInfo=context.getSharedPreferences("loginInfo",MODE_PRIVATE);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        AndroidNetworking.initialize(context);

        Glide.with(context).load(dataList.get(position).userDpUrl).circleCrop().placeholder(R.drawable.blank_profile).into(holder.userProfileImage);
        holder.username.setText(dataList.get(position).username);
        holder.comment.setText(dataList.get(position).comment);
        if(dataList.get(position).isLiked){
            holder.likeBtn.setImageResource(R.drawable.red_heart_active_icon);
        }else {
            holder.likeBtn.setImageResource(R.drawable.heart_inactive_icon);
        }
        holder.likes_count_text.setText(Integer.toString(dataList.get(position).likesCount));

        /// like a commetn
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLikedByMe=dataList.get(position).isLiked;
                holder.likeBtn.setEnabled(false);
                /// dislike this comment
                if(isLikedByMe){
                    holder.likeBtn.setImageResource(R.drawable.heart_inactive_icon);
                    PostCommentsDataStructure object=dataList.get(position);
                    dataList.set(position,new PostCommentsDataStructure(object.commentId, object.postId,object.likesCount-1,0,object.userId,object.username,object.userDpUrl,object.comment,object.createdAt));
                    notifyItemChanged(position);
                    String unlikeUrl=url.concat("/like/unlikeAcomment/".concat(Integer.toString(dataList.get(position).commentId)));
                    AndroidNetworking.get(unlikeUrl).setPriority(Priority.HIGH).addHeaders("Authorization", "Bearer ".concat(loginInfo.getString("token","null"))).build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            holder.likeBtn.setEnabled(true);
                            isLikedByMe=false;
                        }

                        @Override
                        public void onError(ANError anError) {
                            holder.likeBtn.setImageResource(R.drawable.red_heart_active_icon);
                            PostCommentsDataStructure object=dataList.get(position);
                            dataList.set(position,new PostCommentsDataStructure(object.commentId, object.postId,object.likesCount+1,1,object.userId,object.username,object.userDpUrl,object.comment,object.createdAt));
                            notifyItemChanged(position);
                            holder.likeBtn.setEnabled(true);
                            isLikedByMe=true;

                        }
                    });


                }
                //like this comment
                else{
                    holder.likeBtn.setImageResource(R.drawable.red_heart_active_icon);
                    PostCommentsDataStructure object=dataList.get(position);
                    dataList.set(position,new PostCommentsDataStructure(object.commentId, object.postId,object.likesCount+1,1,object.userId,object.username,object.userDpUrl,object.comment,object.createdAt));
                    notifyItemChanged(position);
                    String unlikeUrl=url.concat("/like/likeAcomment/".concat(Integer.toString(dataList.get(position).commentId)));
                    AndroidNetworking.get(unlikeUrl).setPriority(Priority.HIGH).addHeaders("Authorization", "Bearer ".concat(loginInfo.getString("token","null"))).build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            holder.likeBtn.setEnabled(true);
                            isLikedByMe=true;
                        }

                        @Override
                        public void onError(ANError anError) {
                            holder.likeBtn.setImageResource(R.drawable.heart_inactive_icon);
                            PostCommentsDataStructure object=dataList.get(position);
                            dataList.set(position,new PostCommentsDataStructure(object.commentId, object.postId,object.likesCount-1,0,object.userId,object.username,object.userDpUrl,object.comment,object.createdAt));
                            notifyItemChanged(position);
                            holder.likeBtn.setEnabled(true);
                            isLikedByMe=false;


                        }
                    });

                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage,likeBtn;
        TextView username,comment,likes_count_text;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage=itemView.findViewById(R.id.userProfile_img);
            username=itemView.findViewById(R.id.username_text);
            comment=itemView.findViewById(R.id.comment_text);
            likeBtn=itemView.findViewById(R.id.like_btn_image);
            likes_count_text=itemView.findViewById(R.id.likes_count_text);

        }
    }
}
