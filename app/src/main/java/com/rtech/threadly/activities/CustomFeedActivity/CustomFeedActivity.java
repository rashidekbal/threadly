package com.rtech.threadly.activities.CustomFeedActivity;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.AllTypePostFeedAdapter;
import com.rtech.threadly.databinding.ActivityCustomFeedBinding;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;
import java.util.List;

public class CustomFeedActivity extends AppCompatActivity {
    ActivityCustomFeedBinding mainXml;
    ArrayList<ExtendedPostModel> postsArray;
    AllTypePostFeedAdapter feedAdapter;
    Intent data;
    boolean isFirstLaunch=true;
    int position;
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainXml=ActivityCustomFeedBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainXml.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        isFirstLaunch=false;
        init();
        setUpViewPager();
    }
    private void init(){
        data=getIntent();
        if(data==null){
            ReUsableFunctions.ShowToast("error occurred");
            super.onBackPressed();
            return;
        }
        postsArray=data.getParcelableArrayListExtra("postList");
        if(postsArray==null){
            ReUsableFunctions.ShowToast("error occurred");
            super.onBackPressed();
            return;
        }
        position=data.getIntExtra("position",0);
        feedAdapter=new AllTypePostFeedAdapter(this,postsArray,position);
    }
    @OptIn(markerClass = UnstableApi.class)
    private void setUpViewPager() {

        List<ExtendedPostModel> postModels;
        postModels=postsArray.subList(position,postsArray.size());
        feedAdapter=new AllTypePostFeedAdapter(this,postModels,position);
        mainXml.viewPager.setOrientation(ORIENTATION_VERTICAL);
        mainXml.viewPager.setAdapter(feedAdapter);
        mainXml.viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @UnstableApi
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        // Stop current playing player if needed
                        ExoplayerUtil.stop();
                        RecyclerView.ViewHolder viewHolder= ((RecyclerView) mainXml.viewPager.getChildAt(0))
                                .findViewHolderForAdapterPosition(position);
                        if (viewHolder != null) {
                            if(viewHolder instanceof AllTypePostFeedAdapter.VideoPostViewHolder){
                                ((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).play_btn.setVisibility(View.GONE);
                                ExoplayerUtil.play(
                                        Uri.parse(postModels.get(position).postUrl),
                                        ((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).videoPlayer_view,((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).previewImageView
                                );
                            }
                        }
                        currentPosition=position;
                    }
                });


    }
    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onResume() {
        super.onResume();
        if(!isFirstLaunch){
            RecyclerView.ViewHolder viewHolder= ((RecyclerView)mainXml.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(currentPosition);
            if(viewHolder instanceof AllTypePostFeedAdapter.VideoPostViewHolder){
                ((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).play_btn.setVisibility(View.GONE);
                ExoplayerUtil.play(
                        Uri.parse(postsArray.get(currentPosition).postUrl),
                        ((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).videoPlayer_view);

            }
        }
    }
}