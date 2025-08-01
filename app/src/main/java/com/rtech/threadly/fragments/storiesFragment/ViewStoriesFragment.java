package com.rtech.threadly.fragments.storiesFragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.adapters.storiesAdapters.StoriesViewpagerAdapter;
import com.rtech.threadly.databinding.FragmentViewStoriesBinding;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.models.StoryMediaModel;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.StoriesViewModel;

import java.util.ArrayList;


public class ViewStoriesFragment extends Fragment {
    FragmentViewStoriesBinding mainXml;
    OnDestroyFragmentCallback callback;
    StoriesViewModel storiesViewModel;
    ArrayList<StoryMediaModel> storiesData;
    String Userid,profilePic;
    StoriesViewpagerAdapter storiesViewpagerAdapter;
    LinearLayoutManager layoutManager;



    public ViewStoriesFragment(OnDestroyFragmentCallback callback) {
        this.callback=callback;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentViewStoriesBinding.inflate(inflater,container,false);
        init();
        storiesViewModel.getStoryOf(Userid).observe(getViewLifecycleOwner(), new Observer<ArrayList<StoryMediaModel>>() {
            @Override
            public void onChanged(ArrayList<StoryMediaModel> storyMediaModels) {
                if(storyMediaModels.isEmpty()){
                    ReUsableFunctions.ShowToast("something went Wrong!");
                    requireActivity().onBackPressed();
                }
                else{
                    mainXml.progressBar.setVisibility(View.GONE);
                    mainXml.viewPager.setVisibility(View.VISIBLE);
                    storiesData.clear();
                    storiesData.addAll(storyMediaModels);

                }
            }});

        return mainXml.getRoot();
    }

    private void init() {
        storiesData=new ArrayList<>();
        storiesViewpagerAdapter=new StoriesViewpagerAdapter(storiesData,requireActivity());
        mainXml.viewPager.setAdapter(storiesViewpagerAdapter);
        mainXml.viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Bundle bundle=getArguments();
        if(bundle!=null){
            Userid=bundle.getString("userId");
            profilePic=bundle.getString("profilePic");
        }else{
            Userid="null";
        }
        storiesViewModel=new ViewModelProvider(requireActivity()).get(StoriesViewModel.class);

        mainXml.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @UnstableApi
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(storiesData.get(position).isVideo()){
                    ExoplayerUtil.stop();
                    StoriesViewpagerAdapter.viewHolder viewHolder =(StoriesViewpagerAdapter.viewHolder)((RecyclerView) mainXml.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
                    if(viewHolder!=null){
                        ExoplayerUtil.play(Uri.parse(storiesData.get(position).getStoryUrl()),viewHolder.playerView);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback.onDestroy();
    }
}