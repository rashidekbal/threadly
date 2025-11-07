package com.rtech.threadly.utils;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rtech.threadly.interfaces.Comments.RecyclerView.replyClick.OnReplyClick;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.network_managers.CommentsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PostCommentsViewerUtil {
    Context context;
    BottomSheetDialog commentDialog;
    CommentsManager commentsManager;
    boolean isReplyMode=false;
    int currentReplyToCommentId=-1;
    int getCurrentReplyToCommentPosition;
    LinearLayout replyToSection;
    ImageView replyToUserProfile;
    TextView replyToUserId;
    ImageView discardReplyToBtn;
    ArrayList<Posts_Comments_Model> commentsList;
    EditText inputComment;
    PostCommentsAdapter postCommentsAdapter;
  public PostCommentsViewerUtil(Context context){
      this.context=context;
      this.commentsManager=new CommentsManager();

  }
  private final OnReplyClick onReplyClick=new OnReplyClick() {
      @Override
      public void ReplyTo(int commentId,int position) {
          isReplyMode=true;
          getCurrentReplyToCommentPosition=position;
          currentReplyToCommentId=commentId;
          setUpReplyToCommentLayout(position);
      }
  };
    private void setUpReplyToCommentLayout(int position) {
        if(!isReplyMode||currentReplyToCommentId==-1){
            replyToSection.setVisibility(View.GONE);
        }else{
            replyToSection.setVisibility(View.VISIBLE);
            Glide.with(context).load(commentsList.get(position).getUserDpUrl()).thumbnail(0.1f).placeholder(R.drawable.blank_profile).circleCrop().into(replyToUserProfile);
            replyToUserId.setText(commentsList.get(position).getUsername());
            inputComment.setText("@"+commentsList.get(position).getUserId()+" ");

        }
        //discard action
        discardReplyToBtn.setOnClickListener(v->{runReplyToCleanUp();
        });



    }

    private void runReplyToCleanUp() {
        isReplyMode=false;
        currentReplyToCommentId=-1;
        replyToSection.setVisibility(View.GONE);
        inputComment.setText(null);
    }

    //setUp comment dialog
    public void setUpCommentDialog(int PostId) {

        commentDialog=new BottomSheetDialog(context, R.style.TransparentBottomSheet);
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
        commentsList=new ArrayList<>();
        postCommentsAdapter=new PostCommentsAdapter(context,commentsList,onReplyClick);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        RecyclerView comments_recyclerView=commentDialog.findViewById(R.id.comments_recyclerView);
        assert comments_recyclerView != null;
        comments_recyclerView.setLayoutManager(layoutManager);
        comments_recyclerView.setAdapter(postCommentsAdapter);
        inputComment=commentDialog.findViewById(R.id.comment_editText);
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
        /// this section is for setting up reply to message functionality
        replyToSection=commentDialog.findViewById(R.id.replyToSection);
        replyToUserProfile=commentDialog.findViewById(R.id.replyToUserProfile);
        replyToUserId=commentDialog.findViewById(R.id.replyToUserId);
        discardReplyToBtn=commentDialog.findViewById(R.id.discardReplyToBtn);









/// reply to message functionality ends here
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
                            commentsList.add(new Posts_Comments_Model(individualComment.getInt("commentid"),
                                    individualComment.getInt("postid"),
                                    individualComment.getInt("comment_likes_count"),
                                    individualComment.getInt("isLiked"),
                                    individualComment.getString("userid"),
                                    individualComment.getString("username"),
                                    individualComment.getString("profilepic"),
                                    individualComment.getString("comment_text"),
                                    individualComment.getString("createdAt"),
                                    individualComment.getInt("replyCount")));
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
            if(isReplyMode){
                try {
                    commentsManager.ReplyToComment(postId, currentReplyToCommentId, commentText, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {
                            posting_progressbar.setVisibility(View.GONE);
                            sendCommentBtn.setVisibility(View.VISIBLE);
                            sendCommentBtn.setClickable(true);
                            runReplyToCleanUp();
                            commentsList.get(getCurrentReplyToCommentPosition).setReplyCount(commentsList.get(getCurrentReplyToCommentPosition).getReplyCount()+1);
                            postCommentsAdapter.notifyItemChanged(getCurrentReplyToCommentPosition);


                        }

                        @Override
                        public void onError(String err) {
                            posting_progressbar.setVisibility(View.GONE);
                            sendCommentBtn.setVisibility(View.VISIBLE);
                            sendCommentBtn.setClickable(true);
                            Log.d("replytoCommentErro", "onError: "+err);
                            ReUsableFunctions.ShowToast("something Went Wrong ..");

                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }else{
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
                                        PreferenceUtil.getUserId(),
                                        PreferenceUtil.getUserName(),
                                        PreferenceUtil.getUserProfilePic(),
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
                                        PreferenceUtil.getUserId(),
                                        PreferenceUtil.getUserName(),
                                        PreferenceUtil.getUserProfilePic(),
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

            }

        });
    }
}
