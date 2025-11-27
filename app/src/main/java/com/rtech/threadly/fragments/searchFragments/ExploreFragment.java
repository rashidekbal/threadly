package com.rtech.threadly.fragments.searchFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.activities.CustomFeedActivity.CustomFeedActivity;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.databinding.FragmentExploreBinding;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.models.ExtendedPostModel;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.viewmodels.ExplorePostsViewModel;

import java.util.ArrayList;


public class ExploreFragment extends Fragment {
    FragmentExploreBinding mainXml;
    ExplorePostsViewModel viewModel;
    ArrayList<Posts_Model> postsModels;
    GridPostAdapter adapter;
    private final Post_fragmentSetCallback callback =new Post_fragmentSetCallback() {
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
    };


    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentExploreBinding.inflate(inflater,container,false);
        init();
        return mainXml.getRoot();
    }

    private void init() {
        postsModels=new ArrayList<>();
        adapter=new GridPostAdapter(requireActivity(),postsModels,callback);
        mainXml.postsRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        mainXml.postsRecyclerView.setAdapter(adapter);
        viewModel=new ViewModelProvider(requireActivity()).get(ExplorePostsViewModel.class);
        loadPosts();
    }

    private void loadPosts() {
        viewModel.getExploreFeed().observe(requireActivity(),posts -> {
            mainXml.progressBar.setVisibility(View.GONE);
          if(posts.isEmpty()){
              handleNoPosts();
              return;

          }
          handlePostsAvailable();
          postsModels.clear();
          postsModels.addAll(posts);
          adapter.notifyDataSetChanged();
        });
    }

    private void handlePostsAvailable() {
        mainXml.postsRecyclerView.setVisibility(View.VISIBLE);
        mainXml.noPostsWarning.setVisibility(View.GONE);
    }

    private void handleNoPosts() {
        mainXml.postsRecyclerView.setVisibility(View.GONE);
        mainXml.noPostsWarning.setVisibility(View.VISIBLE);

    }
}