package com.rtech.threadly.adapters.postsAdapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.messanger.UsersShareSheetGridAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.constants.TypeConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.PostCommentsViewerUtil;
import com.rtech.threadly.utils.PreferenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AllTypePostFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
     List<ExtendedPostModel> postModels;
    Context context;
    int TYPE_IMAGE=0;
    CommentsManager commentsManager;
    FollowManager followManager;
    LikeManager likeManager;
    int position;
    PostCommentsViewerUtil postCommentsViewerUtil;
    public AllTypePostFeedAdapter(Context context,List<ExtendedPostModel> postModels,int position){
        this.postModels=postModels;
        this.context=context;
        this.commentsManager=new CommentsManager();
        this.followManager=new FollowManager();
        this.likeManager=new LikeManager();
        this.position=position;
        this.postCommentsViewerUtil=new PostCommentsViewerUtil(context);
    }
    @Override
    public int getItemViewType(int position) {
        return postModels.get(position).isVideo()?1:TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View v;
        if(viewType==TYPE_IMAGE){
             v=inflater.inflate(R.layout.image_post_reel_layout,parent,false);
             return new ImagePostViewHolder(v);
        }else{
            v=inflater.inflate(R.layout.reel_layout,parent,false);
          return new VideoPostViewHolder(v);
        }

    }


    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        if(viewHolder instanceof ImagePostViewHolder){
            ImagePostViewHolder holder=(ImagePostViewHolder) viewHolder;
            Glide.with(context).load(Uri.parse(postModels.get(position).getPostUrl())).thumbnail(0.1f).into(holder.post_image_view);
            //setUpFollowBtn
            if(postModels.get(position).userId.equals(PreferenceUtil.getUserId())||postModels.get(position).isFollowed()){
                holder.followBtn.setVisibility(View.GONE);
            }
            else{
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setOnClickListener(v -> {
                    holder.followBtn.setEnabled(false);
                    holder.followBtn.setVisibility(View.GONE);
                    followManager.follow(postModels.get(position).userId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            postModels.get(position).setFollowed(true);
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
                });
            }



            holder.is_liked=postModels.get(position).isliked;
            holder.likes=Double.parseDouble(Integer.toString(postModels.get(position).likeCount));
            double comments=Double.parseDouble(Integer.toString(postModels.get(position).commentCount));
            double shares=Double.parseDouble(Integer.toString(postModels.get(position).shareCount));
            if(postModels.get(position).likeCount>0){
                holder.likes_count_text.setVisibility(View.VISIBLE);
            }else{
                holder.likes_count_text.setVisibility(View.GONE);
            }
            if(postModels.get(position).commentCount>0){
                holder.comments_count_text.setVisibility(View.VISIBLE);
            }else{
                holder.comments_count_text.setVisibility(View.GONE);
            }
            if(postModels.get(position).shareCount>0){
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
            Glide.with(context).load(Uri.parse(postModels.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.profile_img);
            holder.username_text.setText(postModels.get(position).userId);
            holder.username_text.setText(postModels.get(position).username);

            if(postModels.get(position).caption.equals("null") || postModels.get(position).caption.isEmpty()){
                holder.caption_text.setVisibility(View.GONE);}else{
                holder.caption_text.setText(postModels.get(position).caption);
            }
            holder.caption_text.setText(postModels.get(position).caption);

            // Show comments dialog on comment button click
            holder.comment_btn_image.setOnClickListener(v-> postCommentsViewerUtil.setUpCommentDialog(postModels.get(position).getPostId()));

            // Set like button image if already liked
            if(postModels.get(position).isliked){
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
            }else{
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
            }

            // Show options dialog on options button click
            holder.optionDots_white.setOnClickListener(v->{
                BottomSheetDialog OptionsDialog =new BottomSheetDialog(context,R.style.TransparentBottomSheet);
                if(postModels.get(position).getUserId().equals(PreferenceUtil.getUserId())){
                    OptionsDialog.setContentView(R.layout.user_post_options_layout);
                    setOptionBtnBehaviourAdmin(OptionsDialog,position);

                }else{
                    OptionsDialog.setContentView(R.layout.posts_action_options_layout);
                }

                setOptionBtnBehaviourNormalUser(OptionsDialog,position);
                OptionsDialog.setCancelable(true);
                OptionsDialog.show();
            });


            // Like/unlike post on like button click
            holder.like_btn_image.setOnClickListener(v->{
                holder.is_liked=postModels.get(position).isliked;
                // Like the post if not already liked
                if(!postModels.get(position).isliked){
                    holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                    holder.likes+=1.0;
                    setLikeCount(holder.likes ,holder);
                    holder.is_liked=true;
                    postModels.get(position).isliked=true;
                    holder.like_btn_image.setEnabled(false);
                    likeManager.likePost(postModels.get(position).postId, new NetworkCallbackInterface() {
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
                            postModels.get(position).isliked=false;
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
                    postModels.get(position).isliked=false;
                    holder.like_btn_image.setEnabled(false);
                    // Send unlike request to server
                    likeManager.UnlikePost(postModels.get(position).postId, new NetworkCallbackInterface() {
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
                            postModels.get(position).isliked=true;
                            holder.like_btn_image.setEnabled(true);

                        }
                    });
                }
            });

            // open userProfile by clicking userProfilePic
            holder.profile_img.setOnClickListener(v -> ReUsableFunctions.openProfile(context,postModels.get(position).userId));
//        or by clicking userid
            holder.username_text.setOnClickListener(v -> ReUsableFunctions.openProfile(context,postModels.get(position).userId));



            holder.share_icon_white.setOnClickListener(V-> OpenPostShareDialog(postModels.get(position)));





        }
        else{
            VideoPostViewHolder holder=(VideoPostViewHolder) viewHolder;
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

         //follow button visibility and functionality
            if(postModels.get(position).userId.equals(PreferenceUtil.getUserId())||postModels.get(position).isFollowed()){
                holder.followBtn.setVisibility(View.GONE);
            }
            else{
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setOnClickListener(v -> {
                    holder.followBtn.setEnabled(false);
                    holder.followBtn.setVisibility(View.GONE);
                    followManager.follow(postModels.get(position).userId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            postModels.get(position).setFollowed(true);
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
                });
            }



            holder.is_liked=postModels.get(position).isliked;
            holder.likes=Double.parseDouble(Integer.toString(postModels.get(position).likeCount));
            double comments=Double.parseDouble(Integer.toString(postModels.get(position).commentCount));
            double shares=Double.parseDouble(Integer.toString(postModels.get(position).shareCount));
            if(postModels.get(position).likeCount>0){
                holder.likes_count_text.setVisibility(View.VISIBLE);
            }else{
                holder.likes_count_text.setVisibility(View.GONE);
            }
            if(postModels.get(position).commentCount>0){
                holder.comments_count_text.setVisibility(View.VISIBLE);
            }else{
                holder.comments_count_text.setVisibility(View.GONE);
            }
            if(postModels.get(position).shareCount>0){
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
            Glide.with(context).load(Uri.parse(postModels.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.profile_img);
            holder.username_text.setText(postModels.get(position).userId);
            holder.username_text.setText(postModels.get(position).username);

            if(postModels.get(position).caption.equals("null") || postModels.get(position).caption.isEmpty()){
                holder.caption_text.setVisibility(View.GONE);}else{
                holder.caption_text.setText(postModels.get(position).caption);
            }
            holder.caption_text.setText(postModels.get(position).caption);

            // Show comments dialog on comment button click
            holder.comment_btn_image.setOnClickListener(v->postCommentsViewerUtil.setUpCommentDialog(postModels.get(position).getPostId()));


            // Set like button image if already liked
            if(postModels.get(position).isliked){
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
            }else{
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
            }

            // Show options dialog on options button click
            holder.optionDots_white.setOnClickListener(v->{
                BottomSheetDialog OptionsDialog =new BottomSheetDialog(context,R.style.TransparentBottomSheet);
                if(postModels.get(position).getUserId().equals(PreferenceUtil.getUserId())){
                    OptionsDialog.setContentView(R.layout.user_post_options_layout);
                    setOptionBtnBehaviourAdmin(OptionsDialog ,position);
                }else{
                    OptionsDialog.setContentView(R.layout.posts_action_options_layout);
                    setOptionBtnBehaviourNormalUser(OptionsDialog,position);
                }
                OptionsDialog.setCancelable(true);
                OptionsDialog.show();
            });


            // Like/unlike post on like button click
            holder.like_btn_image.setOnClickListener(v->{
                holder.is_liked=postModels.get(position).isliked;
                // Like the post if not already liked
                if(!postModels.get(position).isliked){
                    holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                    holder.likes+=1.0;
                    setLikeCount(holder.likes ,holder);
                    holder.is_liked=true;
                    postModels.get(position).isliked=true;
                    holder.like_btn_image.setEnabled(false);
                    likeManager.likePost(postModels.get(position).postId, new NetworkCallbackInterface() {
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
                            postModels.get(position).isliked=false;
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
                    postModels.get(position).isliked=false;
                    holder.like_btn_image.setEnabled(false);
                    // Send unlike request to server
                    likeManager.UnlikePost(postModels.get(position).postId, new NetworkCallbackInterface() {
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
                            postModels.get(position).isliked=true;
                            holder.like_btn_image.setEnabled(true);

                        }
                    });
                }
            });

            // open userProfile by clicking userProfilePic
            holder.profile_img.setOnClickListener(v -> ReUsableFunctions.openProfile(context,postModels.get(position).userId));
//        or by clicking userid
            holder.username_text.setOnClickListener(v -> ReUsableFunctions.openProfile(context,postModels.get(position).userId));



            holder.share_icon_white.setOnClickListener(V-> OpenPostShareDialog(postModels.get(position)));


        }


    }

    private void setOptionBtnBehaviourNormalUser(BottomSheetDialog OptionsDialog, int position) {
        LinearLayout downloadBtnLayout=OptionsDialog.findViewById(R.id.download_btn);
        LinearLayout addFavouriteBtnLayout=OptionsDialog.findViewById(R.id.add_favourite_btn);
        LinearLayout unfollowBtnLayout=OptionsDialog.findViewById(R.id.unfollow_btn);
        LinearLayout followBtnLayout=OptionsDialog.findViewById(R.id.follow_btn);
        LinearLayout reportBtnLayout=OptionsDialog.findViewById(R.id.Report_btn);
        assert downloadBtnLayout != null;
        downloadBtnLayout.setOnClickListener(c->{
            DownloadManagerUtil.downloadFromUri(context,Uri.parse(postModels.get(position).postUrl));
            OptionsDialog.dismiss();
        });
        assert addFavouriteBtnLayout != null;
        addFavouriteBtnLayout.setOnClickListener(v -> {
            Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
            OptionsDialog.dismiss();});
        assert reportBtnLayout != null;
        reportBtnLayout.setOnClickListener(v->{
            Toast.makeText(context,"Coming soon",Toast.LENGTH_SHORT).show();
            OptionsDialog.dismiss();
        });
        assert unfollowBtnLayout != null;
        if (postModels.get(position).isFollowed){
            unfollowBtnLayout.setVisibility(View.VISIBLE);
            assert followBtnLayout != null;
            followBtnLayout.setVisibility(View.GONE);

        }else{
            unfollowBtnLayout.setVisibility(View.GONE);
            assert followBtnLayout != null;
            followBtnLayout.setVisibility(View.VISIBLE);
        }
        if(postModels.get(position).userId.equals(Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"))){
            followBtnLayout.setVisibility(View.GONE);
            unfollowBtnLayout.setVisibility(View.GONE);
        }
        followBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.follow(postModels.get(position).userId, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    postModels.get(position).isFollowed=true;
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
            followManager.unfollow(postModels.get(position).userId, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    postModels.get(position).isFollowed=false;
                    notifyItemChanged(position);

                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err);

                }
            });
        });




    }

    private void setOptionBtnBehaviourAdmin(BottomSheetDialog Optionsdialog, int position) {
        LinearLayout download_btn,archive_btn,toggle_like_btn,toggle_share_btn,toggle_commenting_btn,edit_caption_btn,delete_btn;
        download_btn=Optionsdialog.findViewById(R.id.download_btn);
        archive_btn=Optionsdialog.findViewById(R.id.archive_btn);
        toggle_like_btn=Optionsdialog.findViewById(R.id.toggle_like_btn);
        toggle_share_btn=Optionsdialog.findViewById(R.id.toggle_share_btn);
        toggle_commenting_btn=Optionsdialog.findViewById(R.id.toggle_commenting_btn);
        edit_caption_btn=Optionsdialog.findViewById(R.id.edit_caption_btn);
        delete_btn=Optionsdialog.findViewById(R.id.delete_btn);


        assert delete_btn != null;
        delete_btn.setOnClickListener(v->{
            delete_btn.setEnabled(false);
            new AlertDialog.Builder(context).setTitle("Delete post").setMessage("Do you want to delete post")
                    .setCancelable(true)
                    .setPositiveButton("yes", (dialog, which) -> new PostsManager().RemovePost(postModels.get(position).getPostId(), new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            dialog.dismiss();
                            delete_btn.setEnabled(true);
                            ReUsableFunctions.ShowToast("post remove success");
                            Optionsdialog.hide();
                            postModels.remove(position);
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onError(String err) {
                            delete_btn.setEnabled(true);
                            Optionsdialog.hide();
                            dialog.dismiss();
                            ReUsableFunctions.ShowToast("Something went wrong ..");

                        }
                    })).setNegativeButton("no", (dialog, which) -> {
                        Optionsdialog.hide();
                        dialog.dismiss();
                        delete_btn.setEnabled(true);

                    }).setCancelable(false).show();



        });
        assert download_btn != null;
        download_btn.setOnClickListener(v->{
            DownloadManagerUtil.downloadFromUri(context,Uri.parse(postModels.get(position).getPostUrl()));
            Optionsdialog.dismiss();
        });



    }


    @Override
    public int getItemCount() {
        return postModels.size();
    }
    public static class  ImagePostViewHolder extends RecyclerView.ViewHolder{
        ImageView post_image_view;
        ImageView profile_img,like_btn_image,comment_btn_image,share_icon_white,optionDots_white;
        TextView username_text,caption_text,likes_count_text,comments_count_text,shares_count_text;
        boolean is_liked;
        Double likes;
        AppCompatButton followBtn;
        public ImagePostViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image_view=itemView.findViewById(R.id.post_image_view);
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
        }

    }
    public static  class VideoPostViewHolder extends RecyclerView.ViewHolder{
        public PlayerView videoPlayer_view;
       public ImageView play_btn,profile_img,like_btn_image,comment_btn_image,share_icon_white,optionDots_white,previewImageView;
        TextView username_text,caption_text,likes_count_text,comments_count_text,shares_count_text;
        boolean is_liked;
        Double likes;
        boolean []isPlaying={true};
        AppCompatButton followBtn;

        public VideoPostViewHolder(@NonNull View itemView){
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
        }
    }

    // Sets the like count text for a post
    @SuppressLint("SetTextI18n")
    private  void setLikeCount(Double likes, VideoPostViewHolder holder){
        if(likes>1000){
            likes=likes/1000;
            holder.likes_count_text.setText(Integer.toString(likes.intValue()).concat("k"));
        }else{
            holder.likes_count_text.setText(Integer.toString(likes.intValue()));
        }
    }
    // Sets the like count text for a post
    @SuppressLint("SetTextI18n")
    private  void setLikeCount(Double likes, ImagePostViewHolder holder){
        if(likes>1000){
            likes=likes/1000;
            holder.likes_count_text.setText(Integer.toString(likes.intValue()).concat("k"));
        }else{
            holder.likes_count_text.setText(Integer.toString(likes.intValue()));
        }
    }


    /// post share action function
    private void OpenPostShareDialog(Posts_Model post){
        ArrayList<UsersModel> selectedUsers=new ArrayList<>();
        BottomSheetDialog shareBottomSheet=new BottomSheetDialog(context,R.style.TransparentBottomSheet);
        shareBottomSheet.setContentView(R.layout.post_share_layout);
        AppCompatButton sendBtn=shareBottomSheet.findViewById(R.id.sendBtn);
        RelativeLayout actionButtons_rl=shareBottomSheet.findViewById(R.id.actionButtons_rl);
        ImageView search_btn=shareBottomSheet.findViewById(R.id.search_btn);
        EditText search_edit_text=shareBottomSheet.findViewById(R.id.search_edit_text);
        ImageView suggestUsersBtn=shareBottomSheet.findViewById(R.id.suggestUsersBtn);
        RecyclerView Users_List_recyclerView=shareBottomSheet.findViewById(R.id.Users_List_recyclerView);
        LinearLayout Story_add_ll_btn=shareBottomSheet.findViewById(R.id.Story_add_ll_btn);
        ProgressBar progressBar=shareBottomSheet.findViewById(R.id.progressBar);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,3);
        assert Users_List_recyclerView != null;
        Users_List_recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<UsersModel> usersModelList=new ArrayList<>();
        UsersShareSheetGridAdapter adapter=new UsersShareSheetGridAdapter(context, usersModelList, model -> {
            if(selectedUsers.contains(model)){
                selectedUsers.remove(model);
            }else{
                selectedUsers.add(model);
            }
            assert actionButtons_rl != null;
            if(selectedUsers.isEmpty()){
                actionButtons_rl.setVisibility(View.VISIBLE);
                assert sendBtn != null;
                sendBtn.setVisibility(View.GONE);

            }else{
                actionButtons_rl.setVisibility(View.GONE);
                assert sendBtn != null;
                sendBtn.setVisibility(View.VISIBLE);
            }

        });
        Users_List_recyclerView.setAdapter(adapter);



        MessageAbleUsersViewModel messageAbleUsersViewModel=new ViewModelProvider((AppCompatActivity)context).get(MessageAbleUsersViewModel.class);
        messageAbleUsersViewModel.getUsersList().observe((AppCompatActivity) context, usersModels -> {
            if(usersModels.isEmpty()){
                Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show();

            }else{
                usersModelList.clear();
                usersModelList.addAll(usersModels);
                adapter.notifyDataSetChanged();

            }
            assert progressBar != null;
            progressBar.setVisibility(View.GONE);

        });

        //send btn action
        assert sendBtn != null;
        sendBtn.setOnClickListener(v->{
            int postid=post.postId;
            if(!selectedUsers.isEmpty()){
                for(UsersModel model:selectedUsers){
                    try {
                        ReUsableFunctions.ShowToast(" "+postid);
                        Core.sendCtoS(model.getUuid(),"", TypeConstants.POST,post.postUrl,postid,"sent a reel by "+post.username);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                selectedUsers.clear();
                sendBtn.setVisibility(View.GONE);
                assert actionButtons_rl != null;
                actionButtons_rl.setVisibility(View.VISIBLE);

            }
            shareBottomSheet.dismiss();
        });

        shareBottomSheet.show();

    }

}
