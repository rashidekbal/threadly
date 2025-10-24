package com.rtech.threadly.fragments;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.commentsAdapter.PostCommentsAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentPostsBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.network_managers.PostsManager;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class post_fragment extends Fragment {
    FragmentPostsBinding mainXml;
int postId;
PostsManager postsManager;
LikeManager likeManager;
CommentsManager commentsManager;
Posts_Model postData;
SharedPreferences loginInfo;
private final boolean[] isPlaying={true};





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
        assert getArguments() != null;
        postId=getArguments().getInt("postid");
        loadData();

    }


    private void loadData(){
        postsManager.getPostWithId(postId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
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
                Log.d("postGetError", "onError: ".concat(err));

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
        mainXml.commentsCountText.setText(String.valueOf(data.commentCount));
        mainXml.sharesCountText.setText(String.valueOf(data.shareCount));
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
                likeManager.UnlikePost(data.postId, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(String err) {
                        Log.d("unlikeError", "onError: ".concat(err));
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
                likeManager.likePost(data.postId, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(String err) {
                        Log.d("likeError", "onError: ".concat(err));
                        mainXml.likeBtnImage.setImageResource(R.drawable.heart_inactive_icon_white);
                        data.isliked=false;
                        data.likeCount=data.likeCount-1;
                        mainXml.likesCountText.setText(String.valueOf(data.likeCount));

                    }
                });
            }
        });
        mainXml.commentBtnImage.setOnClickListener(v -> showComments());


       mainXml.optionsBtn.setOnClickListener(v-> showOptions(
               data
       ));
    }


    public void showComments() {
        // This method will be used to show comments for the post
        // You can implement a dialog or a new fragment to display comments
       BottomSheetDialog commentDialog=new BottomSheetDialog(requireActivity(),R.style.TransparentBottomSheet);
        commentDialog.setContentView(R.layout.posts_comment_layout);
        commentDialog.setCancelable(true);
        FrameLayout dialogFrame=commentDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if(dialogFrame!=null){
            BottomSheetBehavior<FrameLayout> behaviour=BottomSheetBehavior.from(dialogFrame);
            // Set bottom sheet properties
            behaviour.setDraggable(false);
            behaviour.setState(STATE_EXPANDED);
            behaviour.setFitToContents(true);
        }




        commentDialog.show();
        ArrayList<Posts_Comments_Model> dataList=new ArrayList<>();
        PostCommentsAdapter postCommentsAdapter=new PostCommentsAdapter(requireActivity(),dataList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        RecyclerView comments_recyclerView=commentDialog.findViewById(R.id.comments_recyclerView);
        assert comments_recyclerView != null;
        comments_recyclerView.setLayoutManager(layoutManager);
        comments_recyclerView.setAdapter(postCommentsAdapter);
        EditText inputComment=commentDialog.findViewById(R.id.comment_editText);
        ImageView sendCommentBtn=commentDialog.findViewById(R.id.post_comment_imgBtn);
        ImageView userProfileImg=commentDialog.findViewById(R.id.user_profile);
        TextView posting_progressbar=commentDialog.findViewById(R.id.posting_progress_text);
        ShimmerFrameLayout shimmerFrameLayout=commentDialog.findViewById(R.id.shimmer_comments_holder);
        assert shimmerFrameLayout != null;
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        LinearLayout noCommentsLayout=commentDialog.findViewById(R.id.no_comment_msg_linear_layout);
        comments_recyclerView.setVisibility(View.GONE);
        assert noCommentsLayout != null;
        noCommentsLayout.setVisibility(View.GONE);
        assert userProfileImg != null;
        Glide.with(requireActivity()).load(loginInfo.getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null")).placeholder(R.drawable.blank_profile).error(R.drawable.blank_profile).circleCrop().into(userProfileImg);

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
                            dataList.add(new Posts_Comments_Model(individualComment.getInt("commentid"),individualComment.getInt("postid"),individualComment.getInt("comment_likes_count"),individualComment.getInt("isLiked"),individualComment.getString("userid"),individualComment.getString("username"),individualComment.getString("profilepic"),individualComment.getString("comment_text"),individualComment.getString("createdAt")));
                        }
                        comments_recyclerView.post(postCommentsAdapter::notifyDataSetChanged);
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
                Toast.makeText(requireActivity(), "Error fetching comments: ".concat(Objects.requireNonNull(err)), Toast.LENGTH_SHORT).show();
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
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow( v.getWindowToken(),0);

                sendCommentBtn.setClickable(false);
                sendCommentBtn.setVisibility(View.GONE);
                assert posting_progressbar != null;
                posting_progressbar.setVisibility(View.VISIBLE);
                assert inputComment != null;
                String commentText=inputComment.getText().toString();
                if(commentText.isEmpty()){
                    Toast.makeText(v.getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
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
                            // Add new comment to the top of the list
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                                dataList.addFirst(new Posts_Comments_Model(
                                        commentid,postId,
                                        0,
                                        0,
                                        loginInfo.getString("userid","unknown"),
                                        loginInfo.getString("username","unknown"),
                                        loginInfo.getString("profileUrl","https://res.cloudinary.com/dphwlcyhg/image/upload/v1747240475/ulpdxajfwpwhlt4ntzn5.webp"),
                                        commentText,new Date().toString()));
                                postCommentsAdapter.notifyItemInserted(0);
                                comments_recyclerView.scrollToPosition(0);
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
                        Log.d("commentError", "onError: ".concat(err));

                    }
                });
            }
        });
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
                                            postsManager.RemovePost(data.postId, new NetworkCallbackInterface() {
                                                @Override
                                                public void onSuccess() {
                                                    dialog.dismiss();
                                                    delete_btn.setEnabled(true);
                                                    ReUsableFunctions.ShowToast("post remove success");
                                                    Optionsdialog.hide();
                                                    requireActivity().onBackPressed();
                                                }

                                                @Override
                                                public void onError(String err) {
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