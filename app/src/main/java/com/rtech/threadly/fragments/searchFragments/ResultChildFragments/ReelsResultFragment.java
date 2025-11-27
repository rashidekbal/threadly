package com.rtech.threadly.fragments.searchFragments.ResultChildFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.activities.CustomFeedActivity.CustomFeedActivity;
import com.rtech.threadly.adapters.SearchPage.AccountsAdapter;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.databinding.FragmentReelsResultBinding;
import com.rtech.threadly.fragments.CustomPostFeed.CustomPostFeedFragment;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.SearchViewModel;

import java.util.ArrayList;


public class ReelsResultFragment extends Fragment {
FragmentReelsResultBinding mainXml;
    SearchViewModel viewModel;
    ArrayList<Posts_Model> postsModelArrayList;
    GridPostAdapter adapter;

    public ReelsResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentReelsResultBinding.inflate(inflater,container,false);
        init();
        return mainXml.getRoot();
    }

    private void init() {

        viewModel=new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        postsModelArrayList=new ArrayList<>();
        adapter=new GridPostAdapter(requireActivity(), postsModelArrayList, new Post_fragmentSetCallback() {
            @Override
            public void openPostFragment(ArrayList<Posts_Model> postsArray, int position) {
                ArrayList<ExtendedPostModel> postArrayList=new ArrayList<>();
                for(Posts_Model model:postsArray){
                    postArrayList.add(new ExtendedPostModel(model.getCONTENT_TYPE(),
                            model.getPostId(),
                            model.getUserId(),
                            model.getUsername(),
                            model.getUserDpUrl(),
                            model.getPostUrl(),
                            model.getCaption(),
                            model.getCreatedAt(),
                            model.getLikedBy(),
                            model.getLikeCount(),
                            model.getCommentCount(),
                            model.getShareCount(),
                            model.getIsliked()?1:0,
                            model.isVideo(),
                            model.isFollowed()));
                }
                Intent openReelsIntent=new Intent(requireActivity(), CustomFeedActivity.class);
                openReelsIntent.putExtra("postList",postArrayList);
                openReelsIntent.putExtra("position",position);
                startActivity(openReelsIntent);
            }

            @Override
            public void openEditor() {

            }
        });
        mainXml.recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        mainXml.recyclerView.setAdapter(adapter);
        loadData();
    }
    private void loadData() {
        viewModel.getPostsResult().observe(requireActivity(), postsModels -> {
            if (postsModels == null) {
                mainXml.progressbar.setVisibility(View.VISIBLE);
                mainXml.recyclerView.setVisibility(View.GONE);
                mainXml.noDataText.setVisibility(View.GONE);
                return;
            }
            mainXml.progressbar.setVisibility(View.GONE);
            if (!postsModels.isEmpty()) {
                mainXml.recyclerView.setVisibility(View.VISIBLE);
                mainXml.noDataText.setVisibility(View.GONE);
                postsModelArrayList.clear();
                postsModelArrayList.addAll(postsModels);
                adapter.notifyDataSetChanged();
            } else {
                mainXml.recyclerView.setVisibility(View.GONE);
                mainXml.noDataText.setVisibility(View.VISIBLE);
            }
        });
    }

}