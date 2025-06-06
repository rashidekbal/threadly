package com.rtech.gpgram.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.R;
import com.rtech.gpgram.structures.PostsDataStructure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ImagePostsAdapter extends RecyclerView.Adapter<ImagePostsAdapter.viewHolder> {
    Context context;
    ArrayList<PostsDataStructure> list;
    SharedPreferences loginInfo;
    BottomSheetDialog commentDialog;
    public ImagePostsAdapter(Context c, ArrayList<PostsDataStructure> list,SharedPreferences preferences){
        this.context=c;
        this.list=list;
        this.loginInfo=preferences;
    }
    @NonNull
    @Override
    public ImagePostsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.pic_post_card,parent,false);
        return new viewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).postId;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImagePostsAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.is_liked=list.get(position).isliked;
      holder.likes=Double.parseDouble(Integer.toString(list.get(position).likeCount));
       Double comments=Double.parseDouble(Integer.toString(list.get(position).commentCount));
        Double shares=Double.parseDouble(Integer.toString(list.get(position).shareCount));

       setLikeCount(holder.likes,holder);
        if(comments>1000){
            comments=comments/1000;
            holder.comments_count_text.setText(Integer.toString(comments.intValue()).concat("k"));
        }else{
            holder.comments_count_text.setText(Integer.toString(comments.intValue()));
        }
        if(shares>1000){
            shares=shares/1000;
            holder.shares_count_text.setText(Integer.toString(shares.intValue()).concat("k"));
        }else{
            holder.shares_count_text.setText(Integer.toString(shares.intValue()));
        }

        Glide.with(context).load(Uri.parse(list.get(position).userDpUrl)).placeholder(R.drawable.ic_launcher_background).into(holder.userprofileImg);
        holder.user_id_text.setText(list.get(position).userId);
        holder.user_name_text.setText(list.get(position).username);
        Glide.with(context).load(list.get(position).postUrl).placeholder(R.drawable.post_placeholder).into(holder.post_image);
        holder.caption_text.setText(list.get(position).caption);
        holder.post_creationDate.setText(list.get(position).createdAt.split("T")[0]);
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments(list.get(position).postId);
            }
        });
        if(holder.likes>1){
            holder.likedBy_layout.setVisibility(View.VISIBLE);
            holder.likesCount_text.setText(Integer.toString(holder.likes.intValue()-1));
            holder.likedBy_text.setText(list.get(position).likedBy);
        }
        if(holder.is_liked){

            holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);


        }



        //like on click
        holder.like_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if not liked  like
                if(!holder.is_liked){
                    JSONObject postid =new JSONObject();
                    try {
                        holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                        holder.likes+=1.0;
                        setLikeCount(holder.likes ,holder);
                        holder.is_liked=true;
                        holder.like_btn_image.setEnabled(false);
                        postid.put("postid",list.get(position).postId);


                        AndroidNetworking.post(BuildConfig.BASE_URL.concat("/like/like"))
                                .addHeaders("Authorization", "Bearer ".concat(Objects.requireNonNull(loginInfo.getString("token", null))))
                                .addApplicationJsonBody(postid)
                                .setPriority(Priority.HIGH).build().getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        holder.like_btn_image.setEnabled(true);



                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
                                        holder.likes-=1.0;
                                        setLikeCount(holder.likes,holder);
                                        holder.is_liked=false;
                                        holder.like_btn_image.setEnabled(true);


                                    }
                                });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                  // if like already dislike
                }else {
                    JSONObject postid =new JSONObject();
                    try {
                        postid.put("postid",list.get(position).postId);
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon);
                        holder.likes-=1.0;
                        setLikeCount(holder.likes,holder);
                        holder.is_liked=false;
                        holder.like_btn_image.setEnabled(false);

                        AndroidNetworking.post(BuildConfig.BASE_URL.concat("/like/unlike"))
                                .addHeaders("Authorization", "Bearer ".concat(Objects.requireNonNull(loginInfo.getString("token", null))))
                                .addApplicationJsonBody(postid)
                                .setPriority(Priority.HIGH).build().getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        holder.like_btn_image.setEnabled(true);



                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                                        holder.likes+=1.0;
                                        setLikeCount(holder.likes ,holder);
                                        holder.is_liked=true;
                                        holder.like_btn_image.setEnabled(true);

                                    }
                                });


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView userprofileImg,
                post_image,
                like_btn_image,
                comment_btn;
        TextView user_id_text,
                user_name_text,
                likes_count_text,
                caption_text,
                comments_count_text,
                shares_count_text,
                post_creationDate,
                likedBy_text,
                likesCount_text;
        boolean is_liked;
        Double likes;
        LinearLayout likedBy_layout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
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
            AndroidNetworking.initialize(context);
            commentDialog=new BottomSheetDialog(context,R.style.TransparentBottomSheet);
            commentDialog.setContentView(R.layout.posts_comment_layout);
            commentDialog.setCancelable(true);
            FrameLayout mainframe=commentDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            commentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if(mainframe!=null){
                        BottomSheetBehavior<View> behavior=BottomSheetBehavior.from(mainframe);
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        behavior.setSkipCollapsed(true);
                        mainframe.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                }
            });


        }
    }

    private void showComments(int postId){

        commentDialog.show();
    }


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
