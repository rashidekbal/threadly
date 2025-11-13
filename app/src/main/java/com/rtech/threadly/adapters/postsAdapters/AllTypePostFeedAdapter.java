package com.rtech.threadly.adapters.postsAdapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.widget.AppCompatButton;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.utils.CoilUtil;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.PostCommentsViewerUtil;
import com.rtech.threadly.utils.PostShareHelperUtil;
import com.rtech.threadly.utils.PreferenceUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.List;

public class AllTypePostFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ExtendedPostModel> postModels;
    Context context;
    int TYPE_IMAGE = 0;
    CommentsManager commentsManager;
    FollowManager followManager;
    LikeManager likeManager;
    int position;
    PostCommentsViewerUtil postCommentsViewerUtil;

    public AllTypePostFeedAdapter(Context context, List<ExtendedPostModel> postModels, int position) {
        this.postModels = postModels;
        this.context = context;
        this.commentsManager = new CommentsManager();
        this.followManager = new FollowManager();
        this.likeManager = new LikeManager();
        this.position = position;
        this.postCommentsViewerUtil = new PostCommentsViewerUtil(context);
    }

    @Override
    public int getItemViewType(int position) {
        return postModels.get(position).isVideo() ? 1 : TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v;
        if (viewType == TYPE_IMAGE) {
            v = inflater.inflate(R.layout.image_post_reel_layout, parent, false);
            return new ImagePostViewHolder(v);
        } else {
            v = inflater.inflate(R.layout.reel_layout, parent, false);
            return new VideoPostViewHolder(v);
        }

    }


    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        int CurrentPosition = viewHolder.getLayoutPosition();

        if (viewHolder instanceof ImagePostViewHolder) {
            //image feed binding
            BindImageFeed((ImagePostViewHolder) viewHolder,position);
        }
        else {
        //video feed binding
            BindVideoFeed((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder, CurrentPosition);
        }


    }


    @Override
    public int getItemCount() {
        return postModels.size();
    }
    /// -----------------------   Video Feed Binder -----------------------------///
    private void BindVideoFeed(@NonNull AllTypePostFeedAdapter.VideoPostViewHolder holder, int position) {
        boolean[] isPlaying = {true};
        holder.videoPlayer_view.setPlayer(null);

        //play video accordingly
        if (isPlaying[0]) {
            holder.play_btn.setVisibility(View.GONE);
        } else {
            holder.play_btn.setVisibility(View.VISIBLE);
        }

        //video player onclick listeners
        holder.videoPlayer_view.setOnClickListener(v -> {
            if (isPlaying[0]) {
                ExoplayerUtil.pause();
                isPlaying[0] = false;
                holder.play_btn.setVisibility(View.VISIBLE);
            } else {
                ExoplayerUtil.resume();
                isPlaying[0] = true;
                holder.play_btn.setVisibility(View.GONE);

            }
        });

        //follow button visibility and functionality
        if (shouldShowFollowBtn(position)) {
            holder.followBtn.setVisibility(View.GONE);
        } else {
            holder.followBtn.setVisibility(View.VISIBLE);
            SetupFollowBtn(holder, position);
        }

        //setup like count visibility
        if (shouldShowLikeCount(position)) {
            holder.likes_count_text.setVisibility(View.VISIBLE);
        } else {
            holder.likes_count_text.setVisibility(View.GONE);
        }

        //setUp commentCountVisibility
        if (shouldShowCommentCount(position)) {
            holder.comments_count_text.setVisibility(View.VISIBLE);
        } else {
            holder.comments_count_text.setVisibility(View.GONE);
        }

        //setUp shareCount visibility
        if (shouldShowShareCount(position)) {
            holder.shares_count_text.setVisibility(View.VISIBLE);
        }
        else {
            holder.shares_count_text.setVisibility(View.GONE);
        }

        //set likeCount
        setLikeCount(postModels.get(position).getLikeCount() + 0.0, holder);

        //set comment count
        setCommentCount(holder,position);

        //setShare Count
        setShareCount(holder,position);

        // Load user profile image and userName and userId
        Glide.with(context).load(Uri.parse(postModels.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.profile_img);
        holder.username_text.setText(postModels.get(position).userId);
        holder.username_text.setText(postModels.get(position).username);

        //set up captions
        if (shouldShowCaption(position)) {
            holder.caption_text.setVisibility(View.VISIBLE);
            holder.caption_text.setText(postModels.get(position).caption);
        }
        else {
            holder.caption_text.setVisibility(View.GONE);
        }

        // Set like button image if already liked
        if (isLikedByMe(position)) {
            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
        }
        else {
            holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        //setUp onclick listeners common
        setUpOnclickListeners(holder,position);



    }
    /// -----------------------   Image Feed Binder ----------------------------///
    private void BindImageFeed(@NonNull ImagePostViewHolder holder, int position){
        //load post owner details
        Glide.with(context).load(Uri.parse(postModels.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.profile_img);
        holder.username_text.setText(postModels.get(position).userId);
        holder.username_text.setText(postModels.get(position).username);

        // load post image
        CoilUtil.loadImage(holder.post_image_view, postModels.get(position).getPostUrl());

        //setup captions
        if (shouldShowCaption(position)) {
            holder.caption_text.setVisibility(View.VISIBLE);
            holder.caption_text.setText(postModels.get(position).caption);
        }
        else {
            holder.caption_text.setVisibility(View.GONE);
        }
        //set like btn background
        if (isLikedByMe(position)) {
            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
        } else {
            holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        //setUpFollowBtn
        if (shouldShowFollowBtn(position)) {
            holder.followBtn.setVisibility(View.GONE);
        }
        else {
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

        //setUp like count visibility
        if (shouldShowLikeCount(position)) {
            holder.likes_count_text.setVisibility(View.VISIBLE);
        }
        else {
            holder.likes_count_text.setVisibility(View.GONE);
        }

        //Setup commentCount visibility
        if (shouldShowCommentCount(position)) {
            holder.comments_count_text.setVisibility(View.VISIBLE);
        }
        else {
            holder.comments_count_text.setVisibility(View.GONE);
        }

        //setup share count visibility
        if (shouldShowShareCount(position)) {
            holder.shares_count_text.setVisibility(View.VISIBLE);
        } else {
            holder.shares_count_text.setVisibility(View.GONE);
        }
        //set like Count
        setLikeCount(postModels.get(position).getLikeCount()+0.0, holder);

        //set commentCounts
        setCommentCount(holder,position);

        //set Share counts
        setShareCount(holder,position);

        //setup onclick listeners Common
        setUpOnclickListeners(holder,position);

    }


    /// ------------------------ common helpers -------------///

    private boolean shouldShowLikeCount(int position) {
        return postModels.get(position).getLikeCount() > 0;
    }
    private boolean shouldShowFollowBtn(int position) {
        return postModels.get(position).userId.equals(PreferenceUtil.getUserId()) || postModels.get(position).isFollowed();
    }
    private boolean shouldShowCommentCount(int position) {
        return postModels.get(position).getCommentCount() > 0;
    }
    private boolean shouldShowCaption(int position){
        return!(postModels.get(position).caption.equals("null") || postModels.get(position).caption.isEmpty());
    }
    private boolean shouldShowShareCount(int position) {
        return postModels.get(position).getShareCount() > 0;
    }
    private boolean isLikedByMe(int position){
        return postModels.get(position).getIsliked();
    }


    /// ----------------------- unique yet common helpers -------------------///
    private void setCommentCount(VideoPostViewHolder holder,int position){
        //setup comment and share count for someActions
        double comments = postModels.get(position).getCommentCount();

        // Format comment count
        if (comments > 1000) {
            comments = comments / 1000;
            holder.comments_count_text.setText(Integer.toString((int) comments).concat("k"));
        } else {
            holder.comments_count_text.setText(Integer.toString((int) comments));
        }


    }
    private void setCommentCount(ImagePostViewHolder holder, int position){
        //setup comment and share count for someActions
        double comments = postModels.get(position).getCommentCount();

        // Format comment count
        if (comments > 1000) {
            comments = comments / 1000;
            holder.comments_count_text.setText(Integer.toString((int) comments).concat("k"));
        } else {
            holder.comments_count_text.setText(Integer.toString((int) comments));
        }


    }
    private  void setShareCount(VideoPostViewHolder holder,int position){
        double shares = postModels.get(position).getShareCount();
        // Format share count
        if (shares > 1000) {
            shares = shares / 1000;
            holder.shares_count_text.setText(Integer.toString((int) shares).concat("k"));
        } else {
            holder.shares_count_text.setText(Integer.toString((int) shares));
        }
    }
    private  void setShareCount(ImagePostViewHolder holder,int position){
        double shares = postModels.get(position).getShareCount();
        // Format share count
        if (shares > 1000) {
            shares = shares / 1000;
            holder.shares_count_text.setText(Integer.toString((int) shares).concat("k"));
        } else {
            holder.shares_count_text.setText(Integer.toString((int) shares));
        }
    }
    private void setUpOnclickListeners(@NonNull VideoPostViewHolder holder, int position) {

        // Show comments dialog on comment button click
        holder.comment_btn_image.setOnClickListener(v -> postCommentsViewerUtil.setUpCommentDialog(postModels.get(position).getPostId()));

        // Show options dialog on options button click
        holder.optionDots_white.setOnClickListener(v -> {
            BottomSheetDialog OptionsDialog = new BottomSheetDialog(context, R.style.TransparentBottomSheet);
            if (postModels.get(position).getUserId().equals(PreferenceUtil.getUserId())) {
                OptionsDialog.setContentView(R.layout.user_post_options_layout);
                setOptionBtnBehaviourAdmin(OptionsDialog, position);
            } else {
                OptionsDialog.setContentView(R.layout.posts_action_options_layout);
                setOptionBtnBehaviourNormalUser(OptionsDialog, position);
            }
            OptionsDialog.setCancelable(true);
            OptionsDialog.show();
        });


        // Like/unlike post on like button click
        holder.like_btn_image.setOnClickListener(v -> {
            // Like the post if not already liked
            if (!postModels.get(position).isliked) {
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);

                setLikeCount(postModels.get(position).getLikeCount() + 1.0, holder);
                postModels.get(position).isliked = true;
                holder.like_btn_image.setEnabled(false);
                likeManager.likePost(postModels.get(position).postId, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);

                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);

                        setLikeCount(postModels.get(position).getLikeCount() - 1.0, holder);
                        postModels.get(position).setIsliked(false);
                        holder.like_btn_image.setEnabled(true);

                    }
                });

                // Unlike the post if already liked
            } else {
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);

                setLikeCount(postModels.get(position).getLikeCount() - 1.0, holder);
                postModels.get(position).setIsliked(false);
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
                        setLikeCount(postModels.get(position).getLikeCount() + 1.0, holder);
                        postModels.get(position).setIsliked(true);
                        holder.like_btn_image.setEnabled(true);

                    }
                });
            }
        });

        // open userProfile by clicking userProfilePic
        holder.profile_img.setOnClickListener(v -> ReUsableFunctions.openProfile(context, postModels.get(position).userId));
//        or by clicking userid
        holder.username_text.setOnClickListener(v -> ReUsableFunctions.openProfile(context, postModels.get(position).userId));


        holder.share_icon_white.setOnClickListener(V -> PostShareHelperUtil.OpenPostShareDialog(postModels.get(position),context));


    }
    private void setUpOnclickListeners(@NonNull ImagePostViewHolder holder, int position) {

        // Show comments dialog on comment button click
        holder.comment_btn_image.setOnClickListener(v -> postCommentsViewerUtil.setUpCommentDialog(postModels.get(position).getPostId()));

        // Show options dialog on options button click
        holder.optionDots_white.setOnClickListener(v -> {
            BottomSheetDialog OptionsDialog = new BottomSheetDialog(context, R.style.TransparentBottomSheet);
            if (postModels.get(position).getUserId().equals(PreferenceUtil.getUserId())) {
                OptionsDialog.setContentView(R.layout.user_post_options_layout);
                setOptionBtnBehaviourAdmin(OptionsDialog, position);
            } else {
                OptionsDialog.setContentView(R.layout.posts_action_options_layout);
                setOptionBtnBehaviourNormalUser(OptionsDialog, position);
            }
            OptionsDialog.setCancelable(true);
            OptionsDialog.show();
        });


        // Like/unlike post on like button click
        holder.like_btn_image.setOnClickListener(v -> {
            // Like the post if not already liked
            if (!postModels.get(position).isliked) {
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);

                setLikeCount(postModels.get(position).getLikeCount() + 1.0, holder);
                postModels.get(position).isliked = true;
                holder.like_btn_image.setEnabled(false);
                likeManager.likePost(postModels.get(position).postId, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);

                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);

                        setLikeCount(postModels.get(position).getLikeCount() - 1.0, holder);
                        postModels.get(position).setIsliked(false);
                        holder.like_btn_image.setEnabled(true);

                    }
                });

                // Unlike the post if already liked
            } else {
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);

                setLikeCount(postModels.get(position).getLikeCount() - 1.0, holder);
                postModels.get(position).setIsliked(false);
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
                        setLikeCount(postModels.get(position).getLikeCount() + 1.0, holder);
                        postModels.get(position).setIsliked(true);
                        holder.like_btn_image.setEnabled(true);

                    }
                });
            }
        });

        // open userProfile by clicking userProfilePic
        holder.profile_img.setOnClickListener(v -> ReUsableFunctions.openProfile(context, postModels.get(position).userId));
//        or by clicking userid
        holder.username_text.setOnClickListener(v -> ReUsableFunctions.openProfile(context, postModels.get(position).userId));


        holder.share_icon_white.setOnClickListener(V -> PostShareHelperUtil.OpenPostShareDialog(postModels.get(position),context));


    }
    private void SetupFollowBtn(@NonNull VideoPostViewHolder holder, int position) {
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
    private void setOptionBtnBehaviourNormalUser(@NonNull BottomSheetDialog OptionsDialog, int position) {
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





    private void setOptionBtnBehaviourAdmin(@NonNull BottomSheetDialog Optionsdialog, int position) {
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
    public static class  ImagePostViewHolder extends RecyclerView.ViewHolder{
        ImageView post_image_view;
        ImageView profile_img,like_btn_image,comment_btn_image,share_icon_white,optionDots_white;
        TextView username_text,caption_text,likes_count_text,comments_count_text,shares_count_text;

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


}
