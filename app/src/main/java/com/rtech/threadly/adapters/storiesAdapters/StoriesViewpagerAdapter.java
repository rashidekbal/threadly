package com.rtech.threadly.adapters.storiesAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageDecoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.threadly.R;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.interfaces.StoriesBackAndForthInterface;
import com.rtech.threadly.managers.LikeManager;
import com.rtech.threadly.models.StoryMediaModel;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;
import java.util.Date;

public class StoriesViewpagerAdapter extends RecyclerView.Adapter<StoriesViewpagerAdapter.viewHolder> {

    private final ArrayList<StoryMediaModel> storiesData;
    private final Context context;
    private final LikeManager likeManager;
    private final String userId;
    private final String profilePic;
    private final StoriesBackAndForthInterface backAndForthInterface;

    private boolean isLiked;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    private Player.Listener playerListener;
    private viewHolder currentHolder;
    private int currentPosition = -1;

    public StoriesViewpagerAdapter(ArrayList<StoryMediaModel> storiesData,
                                   Context context,
                                   String userId,
                                   String profilePic,
                                   StoriesBackAndForthInterface backAndForthInterface) {
        this.context = context;
        this.storiesData = storiesData;
        this.likeManager = new LikeManager();
        this.profilePic = profilePic;
        this.userId = userId;
        this.backAndForthInterface = backAndForthInterface;

        // Attach only one listener to ExoPlayer
        initPlayerListener();
        ExoplayerUtil.getExoplayer().addListener(playerListener);
    }

    private void initPlayerListener() {
        playerListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                ExoPlayer player = ExoplayerUtil.getExoplayer();
                if (currentHolder == null) return;

                if (state == Player.STATE_READY && player.getPlayWhenReady()) {
                    startProgressListener(player, currentHolder, currentPosition);
                } else if (state == Player.STATE_ENDED) {
                    stopProgressListener();
                    backAndForthInterface.next(currentPosition, storiesData.size());
                }
            }
        };
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.story_view_layout, parent, false);
        return new viewHolder(v);
    }

    @Override
    public void onViewRecycled(@NonNull viewHolder holder) {
        super.onViewRecycled(holder);
        if (currentHolder == holder) {
            stopProgressListener();
            currentHolder = null;
        }
        holder.playerView.setPlayer(null);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        ExoplayerUtil.getExoplayer().removeListener(playerListener);
        stopProgressListener();
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        currentHolder = holder;
        currentPosition = position;
         holder.time.setText(getHoursAgo(storiesData.get(position).getCreatedAt()));


        stopProgressListener();

        Glide.with(context)
                .load(profilePic)
                .placeholder(R.drawable.blank_profile)
                .circleCrop()
                .into(holder.userProfile_img);

        holder.userid_text_view.setText(userId);
        isLiked = storiesData.get(position).isLiked();

        if (!storiesData.get(position).isVideo()) {
            ExoplayerUtil.stop();
            holder.play_btn.setVisibility(View.GONE);
            holder.post_image_view.setVisibility(View.VISIBLE);
            holder.playerView.setVisibility(View.GONE);
            Glide.with(context)
                    .load(storiesData.get(position).getStoryUrl())
                    .placeholder(R.drawable.post_placeholder)
                    .into(holder.post_image_view);
        } else {
            boolean[] isPlaying = {true};
            holder.play_btn.setVisibility(View.GONE);
            holder.post_image_view.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.VISIBLE);

            holder.playerView.setOnClickListener(v -> {
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
        }

        holder.like_btn_image.setImageResource(isLiked ?
                R.drawable.red_heart_active_icon : R.drawable.heart_inactive_icon_white);

        setOnclickListeners(holder, position);
    }
    String getHoursAgo(String createdAt){
        String createdTime=createdAt.split("T")[1];
        int createdHour=(int)(Float.parseFloat(createdTime.split(":")[0])+6.35);
        String  intermediate=createdAt.split("-")[2];
        intermediate=intermediate.split(" ")[0];
        intermediate=intermediate.split("T")[0];
        int createdDate=Integer.parseInt(intermediate);

        Date currentDate=new Date();
        int currentDay=currentDate.getDate();
        int currentHour=currentDate.getHours();

        if(createdDate==currentDay){
            if(currentHour-createdHour<1){
                return "last hour";
            }
            return Integer.toString(currentHour-createdHour).concat("hr ago");
        }else{
            return Integer.toString(24-createdHour+currentHour).concat("hr ago");
        }}
    private void startProgressListener(ExoPlayer player, viewHolder holder, int position) {
        stopProgressListener(); // Ensure no duplicate runnable
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                long current = player.getCurrentPosition();
                long duration = player.getDuration();

                if (duration > 0) {
                    holder.progressBar.setMax((int) duration);
                    holder.progressBar.setProgress((int) current);

                    if (current >= duration - 150) {
                        ExoplayerUtil.stop();
                        backAndForthInterface.next(position, storiesData.size());
                        return;
                    }
                }
                handler.postDelayed(this, 50);
            }
        };
        handler.post(progressRunnable);
    }

    private void stopProgressListener() {
        if (progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
            progressRunnable = null;
        }
    }

    private void setOnclickListeners(viewHolder holder, int position) {
        holder.next_btn.setOnClickListener(v ->
                backAndForthInterface.next(position, storiesData.size()));

        holder.previous_btn.setOnClickListener(v ->
                backAndForthInterface.previous(position));

        holder.like_btn_image.setOnClickListener(v -> {
            if (isLiked) {
                holder.like_btn_image.setEnabled(false);
                holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
                storiesData.get(position).setLiked(false);
                isLiked = false;

                likeManager.UnLikeStory(storiesData.get(position).getStoryId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);
                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setEnabled(true);
                        holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                        storiesData.get(position).setLiked(true);
                        isLiked = true;
                    }
                });
            } else {
                holder.like_btn_image.setEnabled(false);
                holder.like_btn_image.setImageResource(R.drawable.red_heart_active_icon);
                storiesData.get(position).setLiked(true);
                isLiked = true;

                likeManager.LikeStory(storiesData.get(position).getStoryId(), new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        holder.like_btn_image.setEnabled(true);
                    }

                    @Override
                    public void onError(String err) {
                        holder.like_btn_image.setEnabled(true);
                        holder.like_btn_image.setImageResource(R.drawable.heart_inactive_icon_white);
                        storiesData.get(position).setLiked(false);
                        isLiked = false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return storiesData.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView post_image_view, share_btn, like_btn_image, userProfile_img, play_btn;
        public PlayerView playerView;
        ProgressBar progressBar;
        View previous_btn, next_btn;
        TextView userid_text_view, time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            post_image_view = itemView.findViewById(R.id.post_image_view);
            playerView = itemView.findViewById(R.id.videoPlayer_view);
            progressBar = itemView.findViewById(R.id.progressBar);
            share_btn = itemView.findViewById(R.id.share_btn);
            like_btn_image = itemView.findViewById(R.id.like_btn_image);
            previous_btn = itemView.findViewById(R.id.previous_btn);
            next_btn = itemView.findViewById(R.id.next_btn);
            userid_text_view = itemView.findViewById(R.id.userid);
            time = itemView.findViewById(R.id.time);
            userProfile_img = itemView.findViewById(R.id.userProfile_img);
            play_btn = itemView.findViewById(R.id.play_btn);
        }
    }
}
