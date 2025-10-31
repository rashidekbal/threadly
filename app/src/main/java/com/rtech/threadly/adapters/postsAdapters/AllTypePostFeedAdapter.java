package com.rtech.threadly.adapters.postsAdapters;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
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
import androidx.annotation.OptIn;
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
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AllTypePostFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
     List<ExtendedPostModel> postModels;
    Context context;
    int TYPE_IMAGE=0;
    CommentsManager commentsManager;
    BottomSheetDialog commentDialog;
    SharedPreferences loginInfo=Core.getPreference();
    FollowManager followManager;
    LikeManager likeManager;
    int position;
    public AllTypePostFeedAdapter(Context context,List<ExtendedPostModel> postModels,int position){
        this.postModels=postModels;
        this.context=context;
        this.commentsManager=new CommentsManager();
        this.followManager=new FollowManager();
        this.likeManager=new LikeManager();
        this.position=position;
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

        if(viewHolder.getItemViewType()==TYPE_IMAGE){
            ImagePostViewHolder holder=(ImagePostViewHolder) viewHolder;
            Glide.with(context).load(Uri.parse(postModels.get(position).getPostUrl())).placeholder(R.drawable.post_placeholder).into(holder.post_image_view);
            //setUpFollowBtn
            setUpFollowBtn(holder,position);



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
            holder.comment_btn_image.setOnClickListener(v->setUpCommentDialog(postModels.get(position).postId));

            // Set like button image if already liked
            if(postModels.get(position).isliked){
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
            }else{
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



            holder.share_icon_white.setOnClickListener(V->{
                OpenPostShareDialog(postModels.get(position));

            });





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
            setUpFollowBtn(holder,position);



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
            holder.comment_btn_image.setOnClickListener(v->setUpCommentDialog(postModels.get(position).postId));


            // Set like button image if already liked
            if(postModels.get(position).isliked){
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
            }else{
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



            holder.share_icon_white.setOnClickListener(V->{
                OpenPostShareDialog(postModels.get(position));

            });


        }


    }

    private void setUpFollowBtn(RecyclerView.ViewHolder holderView, int position) {
        if(holderView instanceof VideoPostViewHolder){
            VideoPostViewHolder holder=(VideoPostViewHolder) holderView;
            if(postModels.get(position).isFollowed||postModels.get(position).userId.equals(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"))){
                holder.followBtn.setVisibility(View.GONE);
            }
            else{
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.followBtn.setEnabled(false);
                        holder.followBtn.setVisibility(View.GONE);
                        followManager.follow(postModels.get(position).userId, new NetworkCallbackInterface() {
                            @Override
                            public void onSuccess() {
                                postModels.get(position).isFollowed=true;
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
        }else{
            ImagePostViewHolder holder=(ImagePostViewHolder) holderView;
            if(postModels.get(position).isFollowed||postModels.get(position).userId.equals(loginInfo.getString(SharedPreferencesKeys.USER_ID,"null"))){
                holder.followBtn.setVisibility(View.GONE);
            }
            else{
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.followBtn.setEnabled(false);
                        holder.followBtn.setVisibility(View.GONE);
                        followManager.follow(postModels.get(position).userId, new NetworkCallbackInterface() {
                            @Override
                            public void onSuccess() {
                                postModels.get(position).isFollowed=true;
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
        }


    }


    private void setOptionBtnBehaviour(BottomSheetDialog optionsDialog, int position) {
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
//        if(likes>1000){
//            likes=likes/1000;
//            holder.likes_count_text.setText(Integer.toString(likes.intValue()).concat("k"));
//        }else{
//            holder.likes_count_text.setText(Integer.toString(likes.intValue()));
//        }
    }

//setUp comment dialog
    private void setUpCommentDialog(int PostId) {

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
        showComments(PostId);
    }
    //show comments dialog of a given post
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
        assert currentUserProfileImg != null;
        Glide.with(context).load(Core.getPreference().getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null")).placeholder(R.drawable.blank_profile)
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
                    }else {
                        // Show no comments layout if empty
                        noCommentsLayout.setVisibility(View.VISIBLE);
                        comments_recyclerView.setVisibility(View.GONE);
                    }
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
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
        sendCommentBtn.setOnClickListener(v -> {
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
                            postCommentsAdapter.notifyItemInserted(commentsList.size()-1);
                            comments_recyclerView.scrollToPosition(commentsList.size()-1);
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
        });
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
        Users_List_recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<UsersModel> usersModelList=new ArrayList<>();
        UsersShareSheetGridAdapter adapter=new UsersShareSheetGridAdapter(context, usersModelList, new OnUserSelectedListener() {
            @Override
            public void onSelect(UsersModel model) {
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

            }
        });
        Users_List_recyclerView.setAdapter(adapter);



        MessageAbleUsersViewModel messageAbleUsersViewModel=new ViewModelProvider((AppCompatActivity)context).get(MessageAbleUsersViewModel.class);
        messageAbleUsersViewModel.getUsersList().observe((AppCompatActivity) context, new Observer<ArrayList<UsersModel>>() {
            @Override
            public void onChanged(ArrayList<UsersModel> usersModels) {
                if(usersModels.isEmpty()){
                    Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show();

                }else{
                    usersModelList.clear();
                    usersModelList.addAll(usersModels);
                    adapter.notifyDataSetChanged();

                }
                progressBar.setVisibility(View.GONE);

            }
        });

        //send btn action
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
                actionButtons_rl.setVisibility(View.VISIBLE);

            }
            shareBottomSheet.dismiss();
        });

        shareBottomSheet.show();

    }

}
