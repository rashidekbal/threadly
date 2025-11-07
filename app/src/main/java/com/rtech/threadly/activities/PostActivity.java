package com.rtech.threadly.activities;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
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
import com.rtech.threadly.databinding.ActivityPostBinding;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.PostCommentsViewerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {
    ActivityPostBinding mainXml;
    Intent intentData;
    int postId;
    PostsManager postsManager;
    LikeManager likeManager;
    CommentsManager commentsManager;
    SharedPreferences loginInfo;
    Posts_Model postData;
    FollowManager followManager;
    boolean[] isPlaying={true};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXml=ActivityPostBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainXml.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(0); // clear light status flag
            window.setStatusBarColor(Color.BLACK); // or any dark color you're using

        init();
        loadPost();
        mainXml.profileImg.setOnClickListener(v-> ReUsableFunctions.openProfile(PostActivity.this,postData.getUserId()));
//        also on click of username
        mainXml.usernameText.setOnClickListener(v->ReUsableFunctions.openProfile(PostActivity.this,postData.getUserId()));
        mainXml.shareBtn.setOnClickListener(v->{
            if(postData!=null){
                OpenPostShareDialog(postData);
            }
        });

    }
    private void init(){
        intentData=getIntent();
        postId=intentData.getIntExtra("postid",0);
        postsManager=new PostsManager();
        likeManager=new LikeManager();
        commentsManager=new CommentsManager();
        loginInfo= Core.getPreference();
        followManager=new FollowManager();
    }
    private void loadPost(){
        postsManager.getPostWithId(postId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onSuccess(JSONObject response) {

                JSONArray array = response.optJSONArray("data");
                assert array != null;
                JSONObject object = array.optJSONObject(0);
                int postid = object.optInt("postid");
                String userid = object.optString("userid");
                String imageurl = object.optString("imageurl");
                String caption = object.optString("caption");
                String created_at = object.optString("created_at");
                String username = object.optString("username");
                String profilepic = object.optString("profilepic");
                String likedBy = object.optString("likedBy");
                int likeCount = object.optInt("likeCount");
                int commentCount = object.optInt("commentCount");
                int shareCount = object.optInt("shareCount");
                int isLiked = object.optInt("isLiked");
                boolean isVideo=object.optString("type").equals("video");
                boolean isFollowed=object.optInt("isFollowed")>0;


                postData= new Posts_Model(0,
                        postid,
                        userid,
                        username,
                        profilepic,
                        imageurl,
                        caption,
                        created_at,
                        likedBy,
                        likeCount,
                        commentCount,
                        shareCount,
                        isLiked,
                        isVideo,isFollowed);
                setData(postData);


            }

            @Override
            public void onError(String err) {
                ReUsableFunctions.ShowToast(err);

            }
        });
    }


    @UnstableApi
    private void setData(Posts_Model data) {
        Glide.with(PostActivity.this).load(data.getUserDpUrl()).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profileImg);
        if(data.getCaption().equals("null")||data.getCaption().isEmpty()){
          mainXml.captionText.setVisibility(View.GONE);
        }else{
            mainXml.captionText.setText(data.getCaption());
        }

        mainXml.usernameText.setText(data.getUsername());

        mainXml.likesCountText.setText(String.valueOf(data.getLikeCount()));
        mainXml.commentsCountText.setText(String.valueOf(data.getCommentCount()));
        mainXml.sharesCountText.setText(String.valueOf(data.getShareCount()));
        if (data.getIsliked()) {
            mainXml.likeBtnImage.setImageResource(R.drawable.red_heart_active_icon);

        }else{
            mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        mainXml.likeBtnImage.setOnClickListener(v->{
            if(data.getIsliked()){
//                    unlike
                mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
                data.setIsliked(false);
                data.setLikeCount(data.getLikeCount()-1);
                mainXml.likesCountText.setText(String.valueOf(data.getLikeCount()));
                likeManager.UnlikePost(data.getPostId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(String err) {
                        Log.d("unlikeError", "onError: ".concat(err));
                        mainXml.likeBtnImage.setImageResource(R.drawable.red_heart_active_icon);
                        data.setIsliked(true);
                        data.setLikeCount(data.getLikeCount()+1);
                        mainXml.likesCountText.setText(String.valueOf(data.getLikeCount()));

                    }
                });

            }
            else{
//                    like
                mainXml.likeBtnImage.setImageResource(R.drawable.red_heart_active_icon);
                data.setIsliked(true);
                data.setLikeCount(data.getLikeCount()+1);
                mainXml.likesCountText.setText(String.valueOf(data.getLikeCount()));
                likeManager.likePost(data.getPostId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(String err) {
                        Log.d("likeError", "onError: ".concat(err));
                        mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
                        data.setIsliked(false);
                        data.setLikeCount(data.getLikeCount()-1);
                        mainXml.likesCountText.setText(String.valueOf(data.getLikeCount()));

                    }
                });
            }
        });
        mainXml.commentBtnImage.setOnClickListener(v->new PostCommentsViewerUtil(this).setUpCommentDialog(postId));

        if(data.isVideo()){
            mainXml.videoPlayerView.setVisibility(View.VISIBLE);
            mainXml.postImageView.setVisibility(View.GONE);
            ExoplayerUtil.play(Uri.parse(data.getPostUrl()),mainXml.videoPlayerView);
            mainXml.playBtn.setVisibility(View.GONE);
            mainXml.videoPlayerView.setOnClickListener(v->{
                if(isPlaying[0]){
                    ExoplayerUtil.pause();
                    mainXml.playBtn.setVisibility(View.VISIBLE);
                    isPlaying[0]=false;
                }else{
                    ExoplayerUtil.resume();
                    mainXml.playBtn.setVisibility(View.GONE);
                    isPlaying[0]=true;
                }
            });

        }else{
            mainXml.postImageView.setVisibility(View.VISIBLE);
            mainXml.videoPlayerView.setVisibility(View.GONE);
            Glide.with(PostActivity.this).load(data.getPostUrl()).placeholder(R.drawable.post_placeholder).into(mainXml.postImageView);

        }


        mainXml.optionsBtn.setOnClickListener(v->showOptionsMenu(data));
    }


    public void showOptionsMenu(Posts_Model data){
        BottomSheetDialog optionsDialog=new BottomSheetDialog(this,R.style.TransparentBottomSheet);
        optionsDialog.setContentView(R.layout.posts_action_options_layout);
        optionsDialog.setCancelable(true);
        FrameLayout frameLayout=optionsDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if(frameLayout!=null){
            BottomSheetBehavior<FrameLayout> behavior=BottomSheetBehavior.from(frameLayout);
            behavior.setFitToContents(true);
            behavior.setState(STATE_EXPANDED);
        }
        setOptionBtnsBehaviours(optionsDialog,data);
        optionsDialog.show();
    }

    private void setOptionBtnsBehaviours(BottomSheetDialog OptionsDialog, Posts_Model data) {
        LinearLayout downloadBtn=OptionsDialog.findViewById(R.id.download_btn);
        LinearLayout addFavouriteBtnLayout=OptionsDialog.findViewById(R.id.add_favourite_btn);
        LinearLayout unfollowBtnLayout=OptionsDialog.findViewById(R.id.unfollow_btn);
        LinearLayout followBtnLayout=OptionsDialog.findViewById(R.id.follow_btn);
        LinearLayout reportBtnLayout=OptionsDialog.findViewById(R.id.Report_btn);
        assert downloadBtn != null;
        downloadBtn.setOnClickListener(v->{
            DownloadManagerUtil.downloadFromUri(this,Uri.parse(data.getPostUrl()));
            OptionsDialog.dismiss();
        });
        assert addFavouriteBtnLayout != null;
        addFavouriteBtnLayout.setOnClickListener(v -> {
            Toast.makeText(PostActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
            OptionsDialog.dismiss();});
        assert reportBtnLayout != null;
        reportBtnLayout.setOnClickListener(v->{
            Toast.makeText(PostActivity.this,"Coming soon",Toast.LENGTH_SHORT).show();
            OptionsDialog.dismiss();
        });
        assert unfollowBtnLayout != null;
        if (data.isFollowed()){
            unfollowBtnLayout.setVisibility(View.VISIBLE);
            assert followBtnLayout != null;
            followBtnLayout.setVisibility(View.GONE);

        }else{
            unfollowBtnLayout.setVisibility(View.GONE);
            assert followBtnLayout != null;
            followBtnLayout.setVisibility(View.VISIBLE);
        }
        if(data.getUserId().equals(Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"))){
            followBtnLayout.setVisibility(View.GONE);
            unfollowBtnLayout.setVisibility(View.GONE);
        }
        followBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.follow(data.getUserId(), new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    data.setFollowed(true);
                    unfollowBtnLayout.setVisibility(View.VISIBLE);
                    followBtnLayout.setVisibility(View.GONE);


                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err);

                }
            });

        });
        unfollowBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.unfollow(data.getUserId(), new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    data.setFollowed(false);
                    unfollowBtnLayout.setVisibility(View.GONE);
                    followBtnLayout.setVisibility(View.VISIBLE);

                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err);

                }
            });
        });

    }
    private void OpenPostShareDialog(Posts_Model post){
        ArrayList<UsersModel> selectedUsers=new ArrayList<>();
        BottomSheetDialog shareBottomSheet=new BottomSheetDialog(this,R.style.TransparentBottomSheet);
        shareBottomSheet.setContentView(R.layout.post_share_layout);
        AppCompatButton sendBtn=shareBottomSheet.findViewById(R.id.sendBtn);
        RelativeLayout actionButtons_rl=shareBottomSheet.findViewById(R.id.actionButtons_rl);
        ImageView search_btn=shareBottomSheet.findViewById(R.id.search_btn);
        EditText search_edit_text=shareBottomSheet.findViewById(R.id.search_edit_text);
        ImageView suggestUsersBtn=shareBottomSheet.findViewById(R.id.suggestUsersBtn);
        RecyclerView Users_List_recyclerView=shareBottomSheet.findViewById(R.id.Users_List_recyclerView);
        LinearLayout Story_add_ll_btn=shareBottomSheet.findViewById(R.id.Story_add_ll_btn);
        ProgressBar progressBar=shareBottomSheet.findViewById(R.id.progressBar);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
        Users_List_recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<UsersModel> usersModelList=new ArrayList<>();
        UsersShareSheetGridAdapter adapter=new UsersShareSheetGridAdapter(this, usersModelList, new OnUserSelectedListener() {
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



        MessageAbleUsersViewModel messageAbleUsersViewModel=new ViewModelProvider(this).get(MessageAbleUsersViewModel.class);
        messageAbleUsersViewModel.getUsersList().observe(this, new Observer<ArrayList<UsersModel>>() {
            @Override
            public void onChanged(ArrayList<UsersModel> usersModels) {
                if(usersModels.isEmpty()){
                    Toast.makeText(PostActivity.this, "No users found", Toast.LENGTH_SHORT).show();

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
            if(!selectedUsers.isEmpty()){
                for(UsersModel model:selectedUsers){
                    try {
                        Core.sendCtoS(model.getUuid(),"", TypeConstants.POST,post.getPostUrl(),post.getPostId(),"sent a reel by "+post.getUsername());

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

    @Override
    protected void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }
}