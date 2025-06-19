package com.rtech.gpgram.adapters;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.gpgram.R;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.managers.CommentsManager;
import com.rtech.gpgram.managers.LikeManager;
import com.rtech.gpgram.models.Posts_Comments_Model;
import com.rtech.gpgram.models.Posts_Model;
import com.rtech.gpgram.models.Suggest_Profile_Model;
import com.rtech.gpgram.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

// Adapter for displaying image posts in a RecyclerView
public class ImagePostsAdapter extends RecyclerView.Adapter<ImagePostsAdapter.viewHolder> {
    Context context;
    ArrayList<Posts_Model> list;
    SharedPreferences loginInfo;
    BottomSheetDialog commentDialog;
    RecyclerView usersCardRecyclerView;
    ArrayList<Suggest_Profile_Model> suggestUsersList=new ArrayList<>();
    LikeManager likeManager;
    CommentsManager commentsManager;

    // Constructor for the adapter
    public ImagePostsAdapter(Context c, ArrayList<Posts_Model> list, SharedPreferences preferences , ArrayList<Suggest_Profile_Model> suggestUsersList){
        this.context=c;
        this.list=list;
        this.loginInfo=preferences;
        this.suggestUsersList=suggestUsersList;
        this.likeManager=new LikeManager(c);
        this.commentsManager=new CommentsManager(c);
    }

    // Inflates the layout for each post item
    @NonNull
    @Override
    public ImagePostsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.dynamic_post_layout,parent,false);
        return new viewHolder(view);
    }

    // Returns unique ID for each item
    @Override
    public long getItemId(int position) {
        return list.get(position).postId;
    }

    // Binds data to each view holder
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImagePostsAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
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

        // Load user profile image and post image using Glide
        Glide.with(context).load(Uri.parse(list.get(position).userDpUrl)).circleCrop().placeholder(R.drawable.blank_profile).into(holder.userprofileImg);
        holder.user_id_text.setText(list.get(position).userId);
        holder.user_name_text.setText(list.get(position).username);
        Glide.with(context).load(list.get(position).postUrl).placeholder(R.drawable.post_placeholder).into(holder.post_image);
        holder.caption_text.setText(list.get(position).caption);
        holder.post_creationDate.setText(list.get(position).createdAt.split("T")[0]);

        // Show comments dialog on comment button click
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments(list.get(position).postId);
            }
        });

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

        // Show suggested users banner at position 7
        if(position==7 && !suggestUsersList.isEmpty()){
            holder.banner_txt.setVisibility(View.VISIBLE);
            LinearLayoutManager horizontalLayout=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            suggestUsersAdapter suggestAdapter=new suggestUsersAdapter(context,suggestUsersList);
            suggestAdapter.setHasStableIds(true);
            suggestAdapter.notifyDataSetChanged();
            usersCardRecyclerView.setLayoutManager(horizontalLayout);
            usersCardRecyclerView.setAdapter(suggestAdapter);
            usersCardRecyclerView.setVisibility(View.VISIBLE);
        }
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

    }

    // Returns the total number of items
    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder class for post items
    public class viewHolder extends RecyclerView.ViewHolder {
        // UI components for each post
        ImageView userprofileImg,
                post_image,
                like_btn_image,
                comment_btn,
                options_btn;
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
        View itemView;

        // ViewHolder constructor
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize all view IDs
            usersCardRecyclerView=itemView.findViewById(R.id.users_card_recyclerView);
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
            likesCount_text=itemView.findViewById(R.id.likesCount_text);
            likedBy_layout=itemView.findViewById(R.id.likedBy_layout);
            comment_btn=itemView.findViewById(R.id.comment_btn);
            banner_txt=itemView.findViewById(R.id.banner_txt);

            // Initialize networking and comment dialog
            AndroidNetworking.initialize(context);

            commentDialog=new BottomSheetDialog(context,R.style.TransparentBottomSheet);
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
        ShimmerFrameLayout shimmerFrameLayout=commentDialog.findViewById(R.id.shimmer_comments_holder);
        assert shimmerFrameLayout != null;
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        LinearLayout noCommentsLayout=commentDialog.findViewById(R.id.no_comment_msg_linear_layout);
        comments_recyclerView.setVisibility(View.GONE);
        assert noCommentsLayout != null;
        noCommentsLayout.setVisibility(View.GONE);

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
                                        int commentid=response.getInt("commnetid");
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

                    }
                });
            }
        });
    }

    // Sets the like count text for a post
    @SuppressLint("SetTextI18n")
    private  void setLikeCount(Double likes, viewHolder holder){
        if(likes>1000){
            likes=likes/1000;
            holder.likes_count_text.setText(Integer.toString(likes.intValue()).concat("k"));
        }else{
            holder.likes_count_text.setText(Integer.toString(likes.intValue()));
        }
    }


}