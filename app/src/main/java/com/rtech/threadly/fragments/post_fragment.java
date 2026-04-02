package com.rtech.threadly.fragments;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.SocketIo.SocketEmitterEvents;
import com.rtech.threadly.constants.StatsConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentPostsBinding;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.PostCommentsViewerUtil;
import com.rtech.threadly.utils.PostInteractedByViewerUtil;
import com.rtech.threadly.utils.PostShareHelperUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONObject;


public class post_fragment extends Fragment {
    FragmentPostsBinding mainXml;
int postId;
PostsManager postsManager;
LikeManager likeManager;
CommentsManager commentsManager;
Posts_Model postData;
SharedPreferences loginInfo;
private final boolean[] isPlaying={true};
PostInteractedByViewerUtil postInteractedByViewerUtil;





    public post_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentPostsBinding.inflate(inflater,container,false);
        postsManager=new PostsManager();
        likeManager=new LikeManager();
        commentsManager=new CommentsManager();
        loginInfo= Core.getPreference();
        init();
        // Inflate the layout for this fragment
        Glide.with(requireActivity()).load(R.drawable.post_placeholder).into(mainXml.postImageView);
//        open profile on click of profile pic
        mainXml.profileImg.setOnClickListener(v -> ReUsableFunctions.openProfile(v.getContext(),postData.userId));
//        also on click of username
        mainXml.usernameText.setOnClickListener(v -> ReUsableFunctions.openProfile(v.getContext(),postData.userId));

        return mainXml.getRoot();
    }

    private void init() {
        this.postInteractedByViewerUtil=new PostInteractedByViewerUtil(postsManager,requireActivity());
        assert getArguments() != null;
        postId=getArguments().getInt("postid");
        loadData();


    }


    private void loadData(){
        postsManager.getPostWithId(postId, new NetworkCallbackInterfaceJsonObject() {
            @UnstableApi
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
                int viewCount=object.optInt("viewCount");


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
                        isVideo,isFollowed,false,viewCount);
             setData(postData);



            }

            @Override
            public void onError(int err, JSONObject errorObject) {


            }
        });
    }

    @UnstableApi
    private void  setData( Posts_Model data){

        if(data.isVideo){
            mainXml.videoPlayerView.setVisibility(View.VISIBLE);
            mainXml.postImageView.setVisibility(View.GONE);
            ExoplayerUtil.play(Uri.parse(data.postUrl),mainXml.videoPlayerView);
            if (!isPlaying[0]){
                mainXml.playBtn.setVisibility(View.VISIBLE);
            }else{
                mainXml.playBtn.setVisibility(View.GONE);
            }
            mainXml.videoPlayerView.setOnClickListener(v->{
                if(isPlaying[0]){
                    ExoplayerUtil.pause();
                    isPlaying[0]=false;
                    mainXml.playBtn.setVisibility(View.VISIBLE);
                }else{
                    ExoplayerUtil.resume();
                    isPlaying[0]=true;
                    mainXml.playBtn.setVisibility(View.GONE);
                }
            });




        }else {
            mainXml.postImageView.setVisibility(View.VISIBLE);
            mainXml.videoPlayerView.setVisibility(View.GONE);
            mainXml.playBtn.setVisibility(View.GONE);
            Glide.with(requireActivity()).load(data.postUrl).placeholder(R.drawable.post_placeholder).into(mainXml.postImageView);


        }

        Glide.with(requireActivity()).load(data.userDpUrl).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profileImg);
        mainXml.usernameText.setText(data.username);
        if(data.caption.equals("null")||data.caption.isEmpty()){
            mainXml.captionText.setVisibility(View.GONE);
        }else{
            mainXml.captionText.setText(data.caption);
        }

        mainXml.likesCountText.setText(String.valueOf(data.likeCount));
        mainXml.likesCountText.setOnClickListener(v->handleLikeCountClickHandler(postId));
        mainXml.commentsCountText.setText(String.valueOf(data.commentCount));
        mainXml.sharesCountText.setText(String.valueOf(data.shareCount));
        mainXml.sharesCountText.setOnClickListener(v->handleShareCountClickHandler(postId));
        if (data.isliked) {
            mainXml.likeBtnImage.setImageResource(R.drawable.red_heart_active_icon);

        }else{
            mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        mainXml.likeBtnImage.setOnClickListener(v -> {
            if(data.isliked){
//                    unlike
                mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
                data.isliked=false;
                data.likeCount=data.likeCount-1;
                mainXml.likesCountText.setText(String.valueOf(data.likeCount));
                likeManager.UnlikePost(data.postId, new NetworkCallbackInterfaceJsonObject() {
                    @Override
                    public void onSuccess(JSONObject response) {

                    }

                    @Override      
                    public void onError(int err, JSONObject errorObject) {
                        Log.d("unlikeError", "onError: "+err);
                        mainXml.likeBtnImage.setImageResource(R.drawable.red_heart_active_icon);
                        data.isliked=true;
                        data.likeCount=data.likeCount+1;
                        mainXml.likesCountText.setText(String.valueOf(data.likeCount));

                    }
                });

            }else{
//                    like
                mainXml.likeBtnImage.setImageResource(R.drawable.red_heart_active_icon);
                data.isliked=true;
                data.likeCount=data.likeCount+1;
                mainXml.likesCountText.setText(String.valueOf(data.likeCount));
                likeManager.likePost(data.postId, new NetworkCallbackInterfaceJsonObject() {
                    @Override
                    public void onSuccess(JSONObject response) {

                    }

                    @Override
                    public void onError(int err, JSONObject errorObject) {
                        Log.d("likeError", "onError: "+err);
                        mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
                        data.isliked=false;
                        data.likeCount=data.likeCount-1;
                        mainXml.likesCountText.setText(String.valueOf(data.likeCount));

                    }
                });
            }
        });
        mainXml.commentBtnImage.setOnClickListener(v ->new PostCommentsViewerUtil(requireActivity()).setUpCommentDialog(postId));

         //share btn click listener
        mainXml.shareBtn.setOnClickListener(v->{
            OpenPostShareDialog(postData);
        });
       mainXml.optionsBtn.setOnClickListener(v-> showOptions(
               data
       ));
        if(!postData.isViewed()){
            SocketEmitterEvents.emitPostViewed(postData.getPostId());
            postData.setViewed(true);
        }
    }


    private void OpenPostShareDialog(Posts_Model post){
        PostShareHelperUtil.OpenPostShareDialog(post,requireActivity());
    }



private void showOptions(Posts_Model data){
        BottomSheetDialog optionDialog=new BottomSheetDialog(requireContext(),R.style.TransparentBottomSheet);
        optionDialog.setContentView(R.layout.user_post_options_layout);
        optionDialog.setCancelable(true);
        FrameLayout dialogFrame=optionDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if(dialogFrame!=null){
            BottomSheetBehavior<FrameLayout> behavior=BottomSheetBehavior.from(dialogFrame);
            behavior.setState(STATE_EXPANDED);
            behavior.setFitToContents(true);

        }
        setOptionsBehaviour(optionDialog,data);


        optionDialog.show();

}
private void setOptionsBehaviour(BottomSheetDialog Optionsdialog,Posts_Model data){
        LinearLayout download_btn,archive_btn,toggle_like_btn,toggle_share_btn,toggle_commenting_btn,edit_caption_btn,delete_btn;
        download_btn=Optionsdialog.findViewById(R.id.download_btn);
        archive_btn=Optionsdialog.findViewById(R.id.archive_btn);
        toggle_like_btn=Optionsdialog.findViewById(R.id.toggle_like_btn);
        toggle_share_btn=Optionsdialog.findViewById(R.id.toggle_share_btn);
        toggle_commenting_btn=Optionsdialog.findViewById(R.id.toggle_commenting_btn);
        edit_caption_btn=Optionsdialog.findViewById(R.id.edit_caption_btn);
        delete_btn=Optionsdialog.findViewById(R.id.delete_btn);


        delete_btn.setOnClickListener(v->{
            delete_btn.setEnabled(false);
            new AlertDialog.Builder(requireActivity()).setTitle("Delete post").setMessage("Do you want to delete post")
                            .setCancelable(true)
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            postsManager.RemovePost(data.postId, new NetworkCallbackInterfaceJsonObject() {
                                                @Override
                                                public void onSuccess(JSONObject response) {
                                                    dialog.dismiss();
                                                    delete_btn.setEnabled(true);
                                                    ReUsableFunctions.ShowToast("post remove success");
                                                    Optionsdialog.hide();
                                                    requireActivity().onBackPressed();
                                                }

                                                @Override
                                                public void onError(int err, JSONObject errorObject) {
                                                    delete_btn.setEnabled(true);
                                                    Optionsdialog.hide();
                                                    dialog.dismiss();
                                                    ReUsableFunctions.ShowToast("Something went wrong ..");

                                                }
                                            });

                                        }
                                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Optionsdialog.hide();
                            dialog.dismiss();
                            delete_btn.setEnabled(true);

                        }
                    }).setCancelable(false).show();



        });
        download_btn.setOnClickListener(v->{
            DownloadManagerUtil.downloadFromUri(requireActivity(),Uri.parse(data.postUrl));
            Optionsdialog.dismiss();
        });




}
    private void handleLikeCountClickHandler(int postId){
        postInteractedByViewerUtil.openViewer(StatsConstants.LIKE.toString(),postId);
    }
    private void handleShareCountClickHandler(int postId){
        postInteractedByViewerUtil.openViewer(StatsConstants.SHARE.toString(),postId);
    }
    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExoplayerUtil.stop();
    }
}