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
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
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
import com.rtech.threadly.adapters.mscs.SuggestUsersAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.network_managers.CommentsManager;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.network_managers.LikeManager;
import com.rtech.threadly.models.Posts_Comments_Model;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.LoggerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

// Adapter for displaying image posts in a RecyclerView
public class ImagePostsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int TYPE_SUGGESTION=1;
    int TYPE_CONTENT=0;
    Context context;
    ArrayList<Posts_Model> list;
    SharedPreferences loginInfo;
    BottomSheetDialog commentDialog;
    RecyclerView usersCardRecyclerView;
    ArrayList<Profile_Model_minimal> suggestUsersList=new ArrayList<>();
    LikeManager likeManager;
    CommentsManager commentsManager;
    FollowManager followManager;


    // Constructor for the adapter
    public ImagePostsFeedAdapter(Context c, ArrayList<Posts_Model> list, ArrayList<Profile_Model_minimal> suggestUsersList){
        this.context=c;
        this.list=list;
        this.loginInfo= Core.getPreference();
        this.suggestUsersList=suggestUsersList;
        this.likeManager=new LikeManager();
        this.commentsManager=new CommentsManager();
        this.followManager=new FollowManager();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).CONTENT_TYPE;
    }

    // Inflates the layout for each post item
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view;
        if(viewType==TYPE_SUGGESTION){
            view=inflater.inflate(R.layout.suggest_section_card,parent,false);
            return  new SuggestUsersViewHolder(view);

        }else{
            view=inflater.inflate(R.layout.dynamic_post_layout,parent,false);
            return new ContentViewHolder(view);

        }


        }


    // Returns unique ID for each item
    @Override
    public long getItemId(int position) {
        return list.get(position).postId;
    }

    // Binds data to each view holder
    @OptIn(markerClass = UnstableApi.class)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderView, @SuppressLint("RecyclerView") int position) {
      if(holderView instanceof ContentViewHolder){
          ContentViewHolder holder=(ContentViewHolder)holderView ;
          // Set like, comment, and share counts

          holder.is_liked=list.get(position).isliked;
          holder.likes=Double.parseDouble(Integer.toString(list.get(position).likeCount));
          double comments=Double.parseDouble(Integer.toString(list.get(position).commentCount));
          double shares=Double.parseDouble(Integer.toString(list.get(position).shareCount));

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
          Glide.with(context).load(list.get(position).postUrl).placeholder(R.drawable.post_placeholder).into(holder.post_image);


          // Load user profile image and post image using Glide
          Glide.with(context).load(Uri.parse(list.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.userprofileImg);
          holder.user_id_text.setText(list.get(position).userId);
          holder.user_name_text.setText(list.get(position).username);

          if(list.get(position).caption.equals("null") || list.get(position).caption.isEmpty()){
              holder.caption_text.setVisibility(View.GONE);}else{
              holder.caption_text.setText(list.get(position).caption);
          }
          holder.caption_text.setText(list.get(position).caption);
          holder.post_creationDate.setText(list.get(position).createdAt.split("T")[0]);

          // Show comments dialog on comment button click
          holder.comment_btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  showComments(list.get(position).postId);
              }
          });
          if(list.get(position).likeCount>0){
              holder.likes_count_text.setVisibility(View.VISIBLE);
          }else{
              holder.likes_count_text.setVisibility(View.GONE);
          }


          // Show liked by layout if likes > 1
          if(holder.likes>1){
              holder.likedBy_layout.setVisibility(View.VISIBLE);
              holder.likesCount_text.setText(Integer.toString(holder.likes.intValue()-1));
              holder.likedBy_text.setText(list.get(position).likedBy);
          }

          // Set like button image if already liked
          if(list.get(position).isliked){
              holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
          }else{
              holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
          }

          // Show options dialog on options button click
          holder.options_btn.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View v) {
                  BottomSheetDialog OptionsDialog =new BottomSheetDialog(context,R.style.TransparentBottomSheet);
                  OptionsDialog.setContentView(R.layout.posts_action_options_layout);
                  setOptionsBtnBehaviour(OptionsDialog,position);
                  OptionsDialog.setCancelable(true);
                  OptionsDialog.show();
              }
          });

          // Like/unlike post on like button click
          holder.like_btn_image.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  holder.is_liked=list.get(position).isliked;
                  // Like the post if not already liked
                  if(!list.get(position).isliked){
                      holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                      holder.likes+=1.0;
                      setLikeCount(holder.likes ,holder);
                      holder.is_liked=true;
                      list.get(position).isliked=true;
                      holder.like_btn_image.setEnabled(false);
                      likeManager.likePost(list.get(position).postId, new NetworkCallbackInterface() {
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
                              list.get(position).isliked=false;
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
                      list.get(position).isliked=false;
                      holder.like_btn_image.setEnabled(false);
                      // Send unlike request to server
                      likeManager.UnlikePost(list.get(position).postId, new NetworkCallbackInterface() {
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
                              list.get(position).isliked=true;
                              holder.like_btn_image.setEnabled(true);

                          }
                      });
                  }
              }
          });

          // open userProfile by clicking userProfilepic
          holder.userprofileImg.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  ReUsableFunctions.openProfile(context,list.get(position).userId);

              }
          });

//        or by clicking userid
          holder.user_id_text.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  ReUsableFunctions.openProfile(context,list.get(position).userId);

              }
          });

          // share btn click action
          holder.share_btn.setOnClickListener(v->{
              OpenPostShareDialog();
          });


      }else {
          SuggestUsersAdapter suggestUsersAdapter=new SuggestUsersAdapter(context,suggestUsersList);
          SuggestUsersViewHolder holder=(SuggestUsersViewHolder)holderView;
          // suggestion view section
              holder.mainLayout.setVisibility(View.VISIBLE);
              ((SuggestUsersViewHolder) holderView).SuggestionRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
              ((SuggestUsersViewHolder) holderView).SuggestionRecyclerView.setAdapter(suggestUsersAdapter);
              suggestUsersAdapter.notifyDataSetChanged();
          if (suggestUsersList.isEmpty()) {

              holder.mainLayout.setVisibility(View.GONE);

          }


      }
    }

    private void setOptionsBtnBehaviour(BottomSheetDialog OptionsDialog,int position) {
        LinearLayout downloadBtnLayout=OptionsDialog.findViewById(R.id.download_btn);
        LinearLayout addFavouriteBtnLayout=OptionsDialog.findViewById(R.id.add_favourite_btn);
        LinearLayout unfollowBtnLayout=OptionsDialog.findViewById(R.id.unfollow_btn);
        LinearLayout followBtnLayout=OptionsDialog.findViewById(R.id.follow_btn);
        LinearLayout reportBtnLayout=OptionsDialog.findViewById(R.id.Report_btn);
        downloadBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManagerUtil.downloadFromUri(context,Uri.parse(list.get(position).postUrl));
                OptionsDialog.dismiss();
            }
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
        if (list.get(position).isFollowed){
            unfollowBtnLayout.setVisibility(View.VISIBLE);
            followBtnLayout.setVisibility(View.GONE);

        }else{
            unfollowBtnLayout.setVisibility(View.GONE);
            followBtnLayout.setVisibility(View.VISIBLE);
        }
        if(list.get(position).userId.equals(Core.getPreference().getString(SharedPreferencesKeys.USER_ID,"null"))){
            followBtnLayout.setVisibility(View.GONE);
            unfollowBtnLayout.setVisibility(View.GONE);
        }
        followBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.follow(list.get(position).userId, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    list.get(position).isFollowed=true;
                    notifyItemChanged(position);


                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err.toString());

                }
            });

        });
        unfollowBtnLayout.setOnClickListener(v->{
            OptionsDialog.dismiss();
            followManager.unfollow(list.get(position).userId, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                    list.get(position).isFollowed=false;
                    notifyItemChanged(position);

                }

                @Override
                public void onError(String err) {
                    LoggerUtil.LogNetworkError(err.toString());

                }
            });
        });


    }


    // Returns the total number of items
    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder class for post items
    public class ContentViewHolder extends RecyclerView.ViewHolder {
        // UI components for each post
        ImageView userprofileImg,
                post_image,
                like_btn_image,
                comment_btn,
                options_btn,share_btn;


        TextView user_id_text,
                user_name_text,
                likes_count_text,
                caption_text,
                comments_count_text,
                shares_count_text,
                post_creationDate,
                likedBy_text,
                likesCount_text,
                banner_txt;
        boolean is_liked;
        Double likes;
        LinearLayout likedBy_layout;

        // ViewHolder constructor
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize all view IDs

            options_btn=itemView.findViewById(R.id.options_btn);
            userprofileImg=itemView.findViewById(R.id.userProfile_img);
            user_id_text=itemView.findViewById(R.id.user_id_text);
            user_name_text=itemView.findViewById(R.id.user_name_text);
            post_image=itemView.findViewById(R.id.Content_Image);
            likes_count_text=itemView.findViewById(R.id.likes_count_text);
            like_btn_image=itemView.findViewById(R.id.like_btn_image);
            caption_text=itemView.findViewById(R.id.caption_text);
            comments_count_text=itemView.findViewById(R.id.comments_count_text);
            shares_count_text=itemView.findViewById(R.id.shares_count_text);
            post_creationDate=itemView.findViewById(R.id.post_creationDate);
            likedBy_text=itemView.findViewById(R.id.likedBy_text);
            share_btn=itemView.findViewById(R.id.share_btn);
            likesCount_text=itemView.findViewById(R.id.likesCount_text);
            likedBy_layout=itemView.findViewById(R.id.likedBy_layout);
            comment_btn=itemView.findViewById(R.id.comment_btn);
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

    static class SuggestUsersViewHolder extends RecyclerView.ViewHolder{
        RecyclerView SuggestionRecyclerView;
        RelativeLayout mainLayout;
        public SuggestUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            SuggestionRecyclerView=itemView.findViewById(R.id.suggestionRecyclerView);
            mainLayout=itemView.findViewById(R.id.mainLayout);
        }
    }

    // Shows the comments dialog for a post
    private void showComments(int postId){
        commentDialog.show();
        ArrayList<Posts_Comments_Model> dataList=new ArrayList<>();
        PostCommentsAdapter postCommentsAdapter=new PostCommentsAdapter(context,dataList);
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
                            dataList.add(new Posts_Comments_Model(individualComment.getInt("commentid"),individualComment.getInt("postid"),individualComment.getInt("comment_likes_count"),individualComment.getInt("isLiked"),individualComment.getString("userid"),individualComment.getString("username"),individualComment.getString("profilepic"),individualComment.getString("comment_text"),individualComment.getString("createdAt")));
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
                                            dataList.addFirst(new Posts_Comments_Model(
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
                                            dataList.add(new Posts_Comments_Model(
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

    private  void setLikeCount(Double likes, ContentViewHolder holder){
        if(likes>1000){
            likes=likes/1000;
            (holder).likes_count_text.setText(Integer.toString(likes.intValue()).concat("k"));
        }else{
            (holder).likes_count_text.setText(Integer.toString(likes.intValue()));
        }
    }

    private void OpenPostShareDialog(){
        BottomSheetDialog shareBottomSheet=new BottomSheetDialog(context,R.style.TransparentBottomSheet);
        shareBottomSheet.setContentView(R.layout.post_share_layout);
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

        shareBottomSheet.show();

    }



}