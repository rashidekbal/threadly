package com.rtech.threadly.fragments.CustomPostFeed;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.adapters.postsAdapters.AllTypePostFeedAdapter;
import com.rtech.threadly.adapters.postsAdapters.ReelsAdapter;
import com.rtech.threadly.databinding.FragmentCustomPostFeedBinding;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.ExoplayerUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomPostFeedFragment extends Fragment {
FragmentCustomPostFeedBinding mainXml;
ArrayList<ExtendedPostModel> postsArray=new ArrayList<>();
AllTypePostFeedAdapter feedAdapter;
int position;
    public CustomPostFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            if(getArguments().getParcelableArrayList("postList")!=null){
                postsArray=getArguments().getParcelableArrayList("postList");

            }
            position=getArguments().getInt("position");
        }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainXml=FragmentCustomPostFeedBinding.inflate(inflater,container,false);
        init();

        return mainXml.getRoot();
    }

    private void init(){
        setUpViewPager();

    }

    @OptIn(markerClass = UnstableApi.class)
    private void setUpViewPager() {
        List<ExtendedPostModel> postModels=postsArray.subList(position,postsArray.size()-1);
        feedAdapter=new AllTypePostFeedAdapter(requireActivity(),postModels,position);
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
                        RecyclerView.ViewHolder viewHolder= (RecyclerView.ViewHolder) ((RecyclerView) mainXml.viewPager.getChildAt(0))
                                .findViewHolderForAdapterPosition(position);
                        if (viewHolder != null) {
                            if(viewHolder instanceof AllTypePostFeedAdapter.VideoPostViewHolder){
                                ExoplayerUtil.play(
                                        Uri.parse(postModels.get(position).postUrl),
                                        ((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).videoPlayer_view,((AllTypePostFeedAdapter.VideoPostViewHolder) viewHolder).previewImageView
                                );
                            }
                        }
                    }
                });


    }
    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }


}