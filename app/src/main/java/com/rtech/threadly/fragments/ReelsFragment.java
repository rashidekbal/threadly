package com.rtech.threadly.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.adapters.postsAdapters.ReelsAdapter;
import com.rtech.threadly.databinding.FragmentReelsBinding;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.viewmodels.VideoPostsFeedViewModel;

import java.util.ArrayList;

public class ReelsFragment extends Fragment {
    FragmentReelsBinding mainXml;
    ArrayList<Posts_Model> reelsList=new ArrayList<>();
    VideoPostsFeedViewModel ReelsViewModel;
    ReelsAdapter adapter;
     boolean isFirstLaunch=true;
     int currentPosition;

    public ReelsFragment() {
        // Required empty public constructor
    }





    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentReelsBinding.inflate(inflater,container,false);
        init();



        //observe reels feed
        ReelsViewModel.getLiveVideoPostsFeed().observe(getViewLifecycleOwner(), postsModels -> {
            if(postsModels!=null){
                reelsList.clear();
                reelsList.addAll(postsModels);
                adapter.notifyDataSetChanged();
                mainXml.shimmer.setVisibility(View.GONE);
                mainXml.reelsViewpager.setVisibility(View.VISIBLE);
            }
        });



        //
        mainXml.reelsViewpager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
            @UnstableApi
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Stop current playing player if needed
                ExoplayerUtil.stop();
                ReelsAdapter.viewHolder viewHolder= (ReelsAdapter.viewHolder) ((RecyclerView) mainXml.reelsViewpager.getChildAt(0))
                        .findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    viewHolder.play_btn.setVisibility(View.GONE);
                    ExoplayerUtil.play(
                            Uri.parse(reelsList.get(position).postUrl),
                            viewHolder.videoPlayer_view
                    );
                }
                currentPosition=position;
            }
        });

        return mainXml.getRoot();
    }

    private void init() {
        ReelsViewModel=new ViewModelProvider(requireActivity()).get(VideoPostsFeedViewModel.class);
        adapter=new ReelsAdapter(requireActivity(),reelsList);
        mainXml.reelsViewpager.setAdapter(adapter);
        mainXml.reelsViewpager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        isFirstLaunch=false;
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onResume() {
        super.onResume();
        if(!isFirstLaunch){
            ReelsAdapter.viewHolder viewHolder=(ReelsAdapter.viewHolder) ((RecyclerView)mainXml.reelsViewpager.getChildAt(0)).findViewHolderForAdapterPosition(currentPosition);
            if(viewHolder!=null){
                viewHolder.play_btn.setVisibility(View.GONE);
                ExoplayerUtil.play(
                        Uri.parse(reelsList.get(currentPosition).postUrl),
                        viewHolder.videoPlayer_view
                );
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }
}