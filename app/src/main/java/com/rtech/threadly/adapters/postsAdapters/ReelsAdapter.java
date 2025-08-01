package com.rtech.threadly.adapters.postsAdapters;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.commentsAdapter.PostCommentsAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.managers.CommentsManager;
import com.rtech.threadly.managers.LikeManager;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.viewHolder> {
      ArrayList<Posts_Model> dataList;
      Context context;
      LikeManager likeManager;
      BottomSheetDialog commentDialog;
      SharedPreferences loginInfo;
      CommentsManager commentsManager;


    public ReelsAdapter(Context context, ArrayList<Posts_Model> reelsList) {
        this.dataList=reelsList;
        this.context=context;
        this.likeManager=new LikeManager();
        this.commentsManager=new CommentsManager();

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reel_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onViewRecycled(@NonNull viewHolder holder) {
        super.onViewRecycled(holder);
        holder.videoPlayer_view.setPlayer(null);
        
        

    }

    @UnstableApi
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.videoPlayer_view.setPlayer(null);
        if(holder.isPlaying[0]){
            holder.play_btn.setVisibility(View.GONE);
        }else{
            holder.play_btn.setVisibility(View.VISIBLE);
        }
        holder.videoPlayer_view.setOnClickListener(v->{
            if(holder.isPlaying[0]){
                ExoplayerUtil.pause();
                holder.isPlaying[0]=false;
                holder.play_btn.setVisibility(View.VISIBLE);
            }else {
                ExoplayerUtil.resume();
                holder.isPlaying[0]=true;
                holder.play_btn.setVisibility(View.GONE);

            }
        });





        holder.is_liked=dataList.get(position).isliked;
        holder.likes=Double.parseDouble(Integer.toString(dataList.get(position).likeCount));
        double comments=Double.parseDouble(Integer.toString(dataList.get(position).commentCount));
        double shares=Double.parseDouble(Integer.toString(dataList.get(position).shareCount));
        if(dataList.get(position).likeCount>0){
            holder.likes_count_text.setVisibility(View.VISIBLE);
        }else{
            holder.likes_count_text.setVisibility(View.GONE);
        }
        if(dataList.get(position).commentCount>0){
            holder.comments_count_text.setVisibility(View.VISIBLE);
        }else{
            holder.comments_count_text.setVisibility(View.GONE);
        }
        if(dataList.get(position).shareCount>0){
            holder.shares_count_text.setVisibility(View.VISIBLE);
        }else{
            holder.shares_count_text.setVisibility(View.GONE);
        }

        setLikeCount(holder.likes,holder);

        // Format comment count
        if(comments>1000){
            comments=comments/1000;
            holder.comments_count_text.setText(Integer.toString((int) comments).concat("k"));
        }else{
            holder.comments_count_text.setText(Integer.toString((int) comments));
        }
        // Format share count
        if(shares>1000){
            shares=shares/1000;
            holder.shares_count_text.setText(Integer.toString((int) shares).concat("k"));
        }else{
            holder.shares_count_text.setText(Integer.toString((int) shares));
        }

        // Load user profile image and post image using Glide
        Glide.with(context).load(Uri.parse(dataList.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.profile_img);
        holder.username_text.setText(dataList.get(position).userId);
        holder.username_text.setText(dataList.get(position).username);

        if(dataList.get(position).caption.equals("null") || dataList.get(position).caption.isEmpty()){
            holder.caption_text.setVisibility(View.GONE);}else{
            holder.caption_text.setText(dataList.get(position).caption);
        }
        holder.caption_text.setText(dataList.get(position).caption);

        // Show comments dialog on comment button click
        holder.comment_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments(dataList.get(position).postId);
            }
        });


        // Set like button image if already liked
        if(dataList.get(position).isliked){
            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
        }else{
            holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        // Show options dialog on options button click
        holder.optionDots_white.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BottomSheetDialog OptionsDialog =new BottomSheetDialog(context,R.style.TransparentBottomSheet);
                OptionsDialog.setContentView(R.layout.posts_action_options_layout);
                OptionsDialog.setCancelable(true);
                OptionsDialog.show();
            }
        });


        // Like/unlike post on like button click
        holder.like_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.is_liked=dataList.get(position).isliked;
                // Like the post if not already liked
                if(!dataList.get(position).isliked){
                    holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                    holder.likes+=1.0;
                    setLikeCount(holder.likes ,holder);
                    holder.is_liked=true;
                    dataList.get(position).isliked=true;
                    holder.like_btn_image.setEnabled(false);
                    likeManager.likePost(dataList.get(position).postId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            holder.like_btn_image.setEnabled(true);

                        }

                        @Override
                        public void onError(String err) {
                            holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
                            holder.likes-=1.0;
                            setLikeCount(holder.likes,holder);
                            holder.is_liked=false;
                            dataList.get(position).isliked=false;
                            holder.like_btn_image.setEnabled(true);

                        }
                    });

                    // Unlike the post if already liked
                }
                else {
                    holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
                    holder.likes-=1.0;
                    setLikeCount(holder.likes,holder);
                    holder.is_liked=false;
                    dataList.get(position).isliked=false;
                    holder.like_btn_image.setEnabled(false);
                    // Send unlike request to server
                    likeManager.UnlikePost(dataList.get(position).postId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            holder.like_btn_image.setEnabled(true);

                        }

                        @Override
                        public void onError(String err) {
                            Log.d("errorUnlike", "onError: ".concat(err));
                            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                            holder.likes+=1.0;
                            setLikeCount(holder.likes ,holder);
                            holder.is_liked=true;
                            dataList.get(position).isliked=true;
                            holder.like_btn_image.setEnabled(true);

                        }
                    });
                }
            }
        });

        // open userProfile by clicking userProfilepic
        holder.profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReUsableFunctions.openProfile(context,dataList.get(position).userId);

            }
        });
//        or by clicking userid
        holder.username_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReUsableFunctions.openProfile(context,dataList.get(position).userId);

            }
        });








    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
       public PlayerView videoPlayer_view;
        ImageView play_btn,profile_img,like_btn_image,comment_btn_image,share_icon_white,optionDots_white;
        TextView username_text,caption_text,likes_count_text,comments_count_text,shares_count_text;
        boolean is_liked;
        Double likes;
        boolean []isPlaying={true};

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            videoPlayer_view=itemView.findViewById(R.id.videoPlayer_view);
            play_btn=itemView.findViewById(R.id.play_btn);
            profile_img=itemView.findViewById(R.id.profile_img);
            username_text=itemView.findViewById(R.id.username_text);
            caption_text=itemView.findViewById(R.id.caption_text);
            like_btn_image=itemView.findViewById(R.id.like_btn_image);
            likes_count_text=itemView.findViewById(R.id.likes_count_text);
            comment_btn_image=itemView.findViewById(R.id.comment_btn_image);
            comments_count_text=itemView.findViewById(R.id.comments_count_text);
            share_icon_white=itemView.findViewById(R.id.share_icon_white);
            shares_count_text=itemView.findViewById(R.id.shares_count_text);
            optionDots_white=itemView.findViewById(R.id.optionDots_white);


            loginInfo= Core.getPreference();


            commentDialog=new BottomSheetDialog(context,R.style.TransparentBottomSheet);
            commentDialog.setContentView(R.layout.posts_comment_layout);
            commentDialog.setCancelable(true);
            FrameLayout dialogFrame=commentDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if(dialogFrame!=null){
                BottomSheetBehavior<FrameLayout> behaviour=BottomSheetBehavior.from(dialogFrame);
                // Set bottom sheet properties
                behaviour.setDraggable(true);
                behaviour.setState(STATE_EXPANDED);
                behaviour.setFitToContents(true);

            }

        }
    }

    private void showComments(int postId){
        commentDialog.show();
        ArrayList<Posts_Comments_Model> commentsList=new ArrayList<>();
        PostCommentsAdapter postCommentsAdapter=new PostCommentsAdapter(context,commentsList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        RecyclerView comments_recyclerView=commentDialog.findViewById(R.id.comments_recyclerView);
        assert comments_recyclerView != null;
        comments_recyclerView.setLayoutManager(layoutManager);
        comments_recyclerView.setAdapter(postCommentsAdapter);
        EditText inputComment=commentDialog.findViewById(R.id.comment_editText);
        ImageView sendCommentBtn=commentDialog.findViewById(R.id.post_comment_imgBtn);
        TextView posting_progressbar=commentDialog.findViewById(R.id.posting_progress_text);
        ImageView currentUserProfileImg=commentDialog.findViewById(R.id.user_profile);
        ShimmerFrameLayout shimmerFrameLayout=commentDialog.findViewById(R.id.shimmer_comments_holder);
        assert shimmerFrameLayout != null;
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        LinearLayout noCommentsLayout=commentDialog.findViewById(R.id.no_comment_msg_linear_layout);
        comments_recyclerView.setVisibility(View.GONE);
        assert noCommentsLayout != null;
        noCommentsLayout.setVisibility(View.GONE);
        Glide.with(context).load(loginInfo.getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null")).placeholder(R.drawable.blank_profile)
                .error(R.drawable.blank_profile).circleCrop().into(currentUserProfileImg);

        // Fetch comments from server
        commentsManager.getCommentOf(postId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray data=response.getJSONArray("data");
                    if(data.length()>0){
                        noCommentsLayout.setVisibility(View.GONE);
                        comments_recyclerView.setVisibility(View.VISIBLE);
                        // Add each comment to the list
                        for (int i=0;i<data.length();i++){
                            JSONObject individualComment=data.getJSONObject(i);
                            commentsList.add(new Posts_Comments_Model(individualComment.getInt("commentid"),individualComment.getInt("postid"),individualComment.getInt("comment_likes_count"),individualComment.getInt("isLiked"),individualComment.getString("userid"),individualComment.getString("username"),individualComment.getString("profilepic"),individualComment.getString("comment_text"),individualComment.getString("createdAt")));
                        }
                        postCommentsAdapter.notifyDataSetChanged();
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }else {
                        // Show no comments layout if empty
                        noCommentsLayout.setVisibility(View.VISIBLE);
                        comments_recyclerView.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                Toast.makeText(context, "Error fetching comments: ".concat(Objects.requireNonNull(err)), Toast.LENGTH_SHORT).show();
                shimmerFrameLayout.stopShimmer();
                noCommentsLayout.setVisibility(View.VISIBLE);
                comments_recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.GONE);

            }
        });
        // Add comment to the post
        assert sendCommentBtn != null;
        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow( v.getWindowToken(),0);

                sendCommentBtn.setClickable(false);
                sendCommentBtn.setVisibility(View.GONE);
                assert posting_progressbar != null;
                posting_progressbar.setVisibility(View.VISIBLE);
                assert inputComment != null;
                String commentText=inputComment.getText().toString();
                if(commentText.isEmpty()){
                    Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentsManager.addComment(postId, commentText, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        posting_progressbar.setVisibility(View.GONE);
                        sendCommentBtn.setVisibility(View.VISIBLE);
                        sendCommentBtn.setClickable(true);
                        try{
                            JSONObject data=response.getJSONObject("data");
                            int commentid=data.getInt("commentid");
                            Log.d("cmnt", "onSuccess: ");

                            // Add new comment to the top of the list
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                                commentsList.addFirst(new Posts_Comments_Model(
                                        commentid,
                                        postId,
                                        0,
                                        0,
                                        loginInfo.getString("userid","unknown"),
                                        loginInfo.getString("username","unknown"),
                                        loginInfo.getString("profileUrl","https://res.cloudinary.com/dphwlcyhg/image/upload/v1747240475/ulpdxajfwpwhlt4ntzn5.webp"),
                                        commentText,
                                        new Date().toString()));
                                postCommentsAdapter.notifyItemInserted(0);
                                comments_recyclerView.scrollToPosition(0);
                            }else{
                                commentsList.add(new Posts_Comments_Model(
                                        commentid,
                                        postId,
                                        0,
                                        0,
                                        loginInfo.getString("userid","unknown"),
                                        loginInfo.getString("username","unknown"),
                                        loginInfo.getString("profileUrl","https://res.cloudinary.com/dphwlcyhg/image/upload/v1747240475/ulpdxajfwpwhlt4ntzn5.webp"),
                                        commentText,
                                        new Date().toString()));
                                postCommentsAdapter.notifyItemInserted(dataList.size()-1);
                                comments_recyclerView.scrollToPosition(dataList.size()-1);
                            }
                            inputComment.setText("");
                        }catch(JSONException jsonError){
                            // Handle JSON error
                        }

                    }

                    @Override
                    public void onError(String err) {
                        posting_progressbar.setVisibility(View.GONE);
                        sendCommentBtn.setVisibility(View.VISIBLE);
                        sendCommentBtn.setClickable(true);

                    }
                });
            }
        });
    }
    // Sets the like count text for a post
    @SuppressLint("SetTextI18n")
    private  void setLikeCount(Double likes, ReelsAdapter.viewHolder holder){
        if(likes>1000){
            likes=likes/1000;
            holder.likes_count_text.setText(Integer.toString(likes.intValue()).concat("k"));
        }else{
            holder.likes_count_text.setText(Integer.toString(likes.intValue()));
        }
    }

}
