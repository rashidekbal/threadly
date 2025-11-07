package com.rtech.threadly.adapters.commentsAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.Comments.RecyclerView.replyClick.OnReplyClick;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.viewHolder> {
    boolean isLikedByMe;
    Context context;
    ArrayList<Posts_Comments_Model> dataList;
    LikeManager likeManager;
    OnReplyClick onReplyClickCallback;
    CommentsManager commentsManager;

    public  PostCommentsAdapter (Context c,ArrayList<Posts_Comments_Model> dataList,OnReplyClick onReplyClick){
        this.context=c;
        this.dataList=dataList;
        this.likeManager=new LikeManager();
        this.onReplyClickCallback=onReplyClick;
        this.commentsManager=new CommentsManager();

    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_card_v2,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).userDpUrl).circleCrop().placeholder(R.drawable.blank_profile).into(holder.userProfileImage);
        holder.username.setText(dataList.get(position).getUserId());
        holder.comment.setText(dataList.get(position).comment);
        if(dataList.get(position).isLiked){
            holder.likeBtn.setImageResource(R.drawable.red_heart_active_icon);
        }else {
            holder.likeBtn.setImageResource(R.drawable.heart_inactive_icon);
        }
        holder.likes_count_text.setText(Integer.toString(dataList.get(position).likesCount));

        /// like a comment
        holder.likeBtn.setOnClickListener(v -> {
            isLikedByMe=dataList.get(position).isLiked;
            holder.likeBtn.setEnabled(false);
            /// dislike this comment
            if(isLikedByMe) {
                holder.likeBtn.setImageResource(R.drawable.heart_inactive_icon);
                Posts_Comments_Model object = dataList.get(position);
                dataList.set(position, new Posts_Comments_Model(object.commentId, object.postId, object.likesCount - 1, 0, object.userId, object.username, object.userDpUrl, object.comment, object.createdAt));
                notifyItemChanged(position);
                likeManager.UnLikeAComment(dataList.get(position).commentId, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.likeBtn.setEnabled(true);
                        isLikedByMe = false;

                    }

                    @Override
                    public void onError(String err) {
                        holder.likeBtn.setImageResource(R.drawable.red_heart_active_icon);
                        Posts_Comments_Model object = dataList.get(position);
                        dataList.set(position, new Posts_Comments_Model(object.commentId, object.postId, object.likesCount + 1, 1, object.userId, object.username, object.userDpUrl, object.comment, object.createdAt));
                        notifyItemChanged(position);
                        holder.likeBtn.setEnabled(true);
                        isLikedByMe = true;

                    }
                });
            }
            //like this comment
            else{
                holder.likeBtn.setImageResource(R.drawable.red_heart_active_icon);
                Posts_Comments_Model object=dataList.get(position);
                dataList.set(position,new Posts_Comments_Model(object.commentId, object.postId,object.likesCount+1,1,object.userId,object.username,object.userDpUrl,object.comment,object.createdAt));
                notifyItemChanged(position);
                likeManager.LikeAComment(dataList.get(position).commentId, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.likeBtn.setEnabled(true);
                        isLikedByMe=true;

                    }

                    @Override
                    public void onError(String err) {
                        holder.likeBtn.setImageResource(R.drawable.heart_inactive_icon);
                        Posts_Comments_Model object=dataList.get(position);
                        dataList.set(position,new Posts_Comments_Model(object.commentId, object.postId,object.likesCount-1,0,object.userId,object.username,object.userDpUrl,object.comment,object.createdAt));
                        notifyItemChanged(position);
                        holder.likeBtn.setEnabled(true);
                        isLikedByMe=false;

                    }
                });
            }
        });



//        open userProfile on click of profile pic
        holder.userProfileImage.setOnClickListener(v -> ReUsableFunctions.openProfile(context,dataList.get(position).userId));
        holder.username.setOnClickListener(v -> ReUsableFunctions.openProfile(context,dataList.get(position).userId));
//set reply btn click action
        holder.ReplyBtn.setOnClickListener(v->onReplyClickCallback.ReplyTo(dataList.get(position).getCommentId(),position));
//check and show replies btn
        if(dataList.get(position).getReplyCount()<=0){
            holder.replies_holderLayout.setVisibility(View.GONE);

        }else{
            holder.replies_holderLayout.setVisibility(View.VISIBLE);
        }
        holder.load_replies_btn.setText(encodeReplyLoaderCount(dataList.get(position).getReplyCount()));

        //show replies btn onclick listener
        holder.load_replies_btn.setOnClickListener(v -> {
            if(holder.load_replies_btn.getText().toString().equals(encodeReplyLoaderCount(0))){
                return;
            }
            holder.progress_circular.setVisibility(View.VISIBLE);
           commentsManager.GetCommentReplies(dataList.get(position).getCommentId(), new NetworkCallbackInterfaceWithJsonObjectDelivery() {
               @Override
               public void onSuccess(JSONObject response) {
                   //on data received successFully
                   ArrayList<Posts_Comments_Model> commentRepliesList=new ArrayList<>();
                   try {
                       JSONArray replies=response.getJSONArray("data");
                       if(replies.length()>0){
                           for(int i=0;i<replies.length();i++){
                               JSONObject reply=replies.getJSONObject(i);
                               commentRepliesList.add(new Posts_Comments_Model(
                                       reply.getInt("commentid"),
                                       reply.getInt("postid"),
                                       reply.getInt("comment_likes_count"),
                                       reply.getInt("isLiked"),
                                       reply.getString("userid"),
                                       reply.getString("username"),
                                       reply.getString("profilepic"),
                                       reply.getString("comment_text"),
                                       reply.getString("createdAt")

                               ));

                           }
                           setUpRepliesView(holder,commentRepliesList);

                       }

                   } catch (JSONException e) {
                       Log.d("commentReplyError", "onSuccess: "+e.getMessage());
                       holder.progress_circular.setVisibility(View.GONE);
                   }
               }

               @Override
               public void onError(String err) {
                   Log.d("commentReplyError", "onSuccess: "+err);
                   holder.progress_circular.setVisibility(View.GONE);


               }
           });


        });



    }

    private void setUpRepliesView(viewHolder holder, ArrayList<Posts_Comments_Model> commentRepliesList) {
        holder.progress_circular.setVisibility(View.GONE);
        holder.repliesRecyclerView.setVisibility(View.VISIBLE);
        holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        CommentRepliesAdapter adapter=new CommentRepliesAdapter(context,commentRepliesList);
        holder.repliesRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        holder.load_replies_btn.setText(encodeReplyLoaderCount(0));
    }

    private String encodeReplyLoaderCount(int replyCount) {
        if(replyCount==0) return "No more Replies";
        if(replyCount==1) return "View Replies";
        return ("View "+replyCount+" more Replies");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage,likeBtn;
        TextView username,comment,likes_count_text,load_replies_btn,ReplyBtn;
        ConstraintLayout replies_holderLayout;
        RecyclerView repliesRecyclerView;
        ProgressBar progress_circular;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage=itemView.findViewById(R.id.userProfile_img);
            username=itemView.findViewById(R.id.username_text);
            comment=itemView.findViewById(R.id.comment_text);
            likeBtn=itemView.findViewById(R.id.like_btn_image);
            likes_count_text=itemView.findViewById(R.id.likes_count_text);
            replies_holderLayout=itemView.findViewById(R.id.replies_holder);
            load_replies_btn=itemView.findViewById(R.id.load_replies_btn);
            ReplyBtn=itemView.findViewById(R.id.ReplyBtn);
            repliesRecyclerView=itemView.findViewById(R.id.repliesRecyclerView);
            progress_circular=itemView.findViewById(R.id.progress_circular);

        }
    }

}
