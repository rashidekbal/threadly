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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.commentsAdapter.PostCommentsAdapter;
import com.rtech.threadly.adapters.messanger.UsersShareSheetGridAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.constants.TypeConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.PostCommentsViewerUtil;
import com.rtech.threadly.utils.PostShareHelperUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

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
      FollowManager followManager;


    public ReelsAdapter(Context context, ArrayList<Posts_Model> reelsList) {
        this.dataList=reelsList;
        this.context=context;
        this.likeManager=new LikeManager();
        this.commentsManager=new CommentsManager();
        this.followManager=new FollowManager();

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
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int rawPosition) {
       int position=holder.getLayoutPosition();
        if(isHavingNext(position)){
            ExoplayerUtil.preloadReel(Uri.parse(dataList.get(position).getPostUrl()));
        }
        holder.videoPlayer_view.setPlayer(null);
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

        if(dataList.get(position).isFollowed||dataList.get(position).userId.equals(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"))){
            holder.followBtn.setVisibility(View.GONE);
        }else{
            holder.followBtn.setVisibility(View.VISIBLE);
            holder.followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.followBtn.setEnabled(false);
                    holder.followBtn.setVisibility(View.GONE);
                    followManager.follow(dataList.get(position).userId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            dataList.get(position).isFollowed=true;
                            holder.followBtn.setEnabled(true);
                            ReUsableFunctions.ShowToast("Following");

                        }

                        @Override
                        public void onError(String err) {
                            holder.followBtn.setVisibility(View.VISIBLE);
                            holder.followBtn.setEnabled(true);
                            ReUsableFunctions.ShowToast("something went wrong..");

                        }
                    });
                }
            });
        }



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
        holder.comment_btn_image.setOnClickListener(v->new PostCommentsViewerUtil(context).setUpCommentDialog(dataList.get(position).postId));


        // Set like button image if already liked
        if(isLikeByMe(position)){
            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
        }
        else{
            holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        // Show options dialog on options button click
        holder.optionDots_white.setOnClickListener(v->{
            BottomSheetDialog OptionsDialog =new BottomSheetDialog(context,R.style.TransparentBottomSheet);
            OptionsDialog.setContentView(R.layout.posts_action_options_layout);
            setOptionBtnBehaviour(OptionsDialog,position);
            OptionsDialog.setCancelable(true);
            OptionsDialog.show();
        });


        // Like/unlike post on like button click
        holder.like_btn_image.setOnClickListener(v->{
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
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive);
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
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive);
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
        });

        // open userProfile by clicking userProfilePic
        holder.profile_img.setOnClickListener(v -> ReUsableFunctions.openProfile(context,dataList.get(position).userId));
//        or by clicking userid
        holder.username_text.setOnClickListener(v -> ReUsableFunctions.openProfile(context,dataList.get(position).userId));



        holder.share_icon_white.setOnClickListener(V->{
            PostShareHelperUtil.OpenPostShareDialog(dataList.get(position),context);

        });








    }

    private boolean isHavingNext(int position) {
        return dataList.size()-1>position;
    }

    private boolean isLikeByMe(int position) {
        return dataList.get(position).getIsliked();
    }

    private void setOptionBtnBehaviour(BottomSheetDialog OptionsDialog, int position) {
        LinearLayout downloadBtnLayout=OptionsDialog.findViewById(R.id.download_btn);
        LinearLayout addFavouriteBtnLayout=OptionsDialog.findViewById(R.id.add_favourite_btn);
        LinearLayout unfollowBtnLayout=OptionsDialog.findViewById(R.id.unfollow_btn);
        LinearLayout followBtnLayout=OptionsDialog.findViewById(R.id.follow_btn);
        LinearLayout reportBtnLayout=OptionsDialog.findViewById(R.id.Report_btn);
        assert downloadBtnLayout != null;
        downloadBtnLayout.setOnClickListener(c->{
            DownloadManagerUtil.downloadFromUri(context,Uri.parse(dataList.get(position).postUrl));
            OptionsDialog.dismiss();
        });
        addFavouriteBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
                OptionsDialog.dismiss();}});
        reportBtnLayout.setOnClickListener(v->{
            Toast.makeText(context,"Coming soon",Toast.LENGTH_SHORT).show();
            OptionsDialog.dismiss();
        });
        if (dataList.get(position).isFollowed){
            unfollowBtnLayout.setVisibility(View.VISIBLE);
            followBtnLayout.setVisibility(View.GONE);

        }else{
            unfollowBtnLayout.setVisibility(View.GONE);
            followBtnLayout.setVisibility(View.VISIBLE);
        }
        if(dataList.get(position).userId.equals(Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"))){
            followBtnLayout.setVisibility(View.GONE);
            unfollowBtnLayout.setVisibility(View.GONE);
        }
        followBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.follow(dataList.get(position).userId, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    dataList.get(position).isFollowed=true;
                    notifyItemChanged(position);


                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err);

                }
            });

        });
        unfollowBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.unfollow(dataList.get(position).userId, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    dataList.get(position).isFollowed=false;
                    notifyItemChanged(position);

                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err);

                }
            });
        });




    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
       public PlayerView videoPlayer_view;
        public ImageView previewImageView, play_btn,profile_img,like_btn_image,comment_btn_image,share_icon_white,optionDots_white;
        TextView username_text,caption_text,likes_count_text,comments_count_text,shares_count_text;
        boolean is_liked;
        Double likes;
        boolean []isPlaying={true};
        AppCompatButton followBtn;

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
            followBtn=itemView.findViewById(R.id.FollowBtn);
            previewImageView=itemView.findViewById(R.id.previewImageView);


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
