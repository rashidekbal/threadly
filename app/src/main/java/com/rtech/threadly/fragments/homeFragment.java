package com.rtech.threadly.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.R;
import com.rtech.threadly.activities.AddStoryActivity;
import com.rtech.threadly.adapters.postsAdapters.ImagePostsFeedAdapter;
import com.rtech.threadly.adapters.storiesAdapters.StatusViewAdapter;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentHomeBinding;
import com.rtech.threadly.interfaces.StoryOpenCallback;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.models.StoriesModel;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.viewmodels.ImagePostsFeedViewModel;
import com.rtech.threadly.viewmodels.StoriesViewModel;
import com.rtech.threadly.viewmodels.VideoPostsFeedViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class homeFragment extends Fragment {

    SharedPreferences loginInfo;
    ArrayList<Posts_Model> posts;
    ArrayList<Profile_Model_minimal> suggestUsersList = new ArrayList<>();
    private ImagePostsFeedViewModel postsViewModel;
    private VideoPostsFeedViewModel videoPostsFeedViewModel;
    FragmentHomeBinding mainXml;
    StoriesViewModel storiesViewModel;
    ArrayList<StoriesModel> storiesData;
    StoryOpenCallback callback;


    public homeFragment(StoryOpenCallback callback) {
        this.callback=callback;
        // Default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout using ViewBinding
        mainXml = FragmentHomeBinding.inflate(inflater, container, false);
        postsViewModel = new ViewModelProvider(requireActivity()).get(ImagePostsFeedViewModel.class);
        videoPostsFeedViewModel=new ViewModelProvider(requireActivity()).get(VideoPostsFeedViewModel.class);
        loginInfo = Core.getPreference();
        storiesViewModel=new ViewModelProvider(requireActivity()).get(StoriesViewModel.class);
        storiesData= new ArrayList<>();

        // -------------------------
        // Setup  stories/status
        // -------------------------
        Glide.with(this).load(loginInfo.getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null")).placeholder(R.drawable.blank_profile).circleCrop().into(mainXml.profileImg);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        StatusViewAdapter StoriesAdapter = new StatusViewAdapter(requireActivity(), storiesData, new StoryOpenCallback() {
            @Override
            public void openStoryOf(String userid,String profilePic,ArrayList<StoriesModel> list,int position) {
                callback.openStoryOf(userid,profilePic,list,position);

            }
        });
        mainXml.storyRecyclerView.setLayoutManager(layoutManager);
        mainXml.storyRecyclerView.setAdapter(StoriesAdapter);

        mainXml.myStoryLayout.setOnClickListener(v->{
            if(mainXml.addStorySymbol.getVisibility()==View.VISIBLE){
                Intent intent=new Intent(requireActivity(),AddStoryActivity.class);
                intent.putExtra("title","New Story");
                startActivity(intent);
            }else{


            }
        });
        // loadStories


        storiesViewModel.getStories().observe(getViewLifecycleOwner(), new Observer<ArrayList<StoriesModel>>() {
            @Override
            public void onChanged(ArrayList<StoriesModel> storiesModels) {

                if(storiesModels.isEmpty()){
                    mainXml.storiesShimmer.setVisibility(View.GONE);
                    mainXml.myStoryLayout.setVisibility(View.VISIBLE);

                }
                else{
                    storiesData.clear();
                    storiesData.addAll(storiesModels);
                    StoriesAdapter.notifyDataSetChanged();
                   mainXml.storiesShimmer.setVisibility(View.GONE);
                   mainXml.storyRecyclerView.setVisibility(View.VISIBLE);
                   mainXml.myStoryLayout.setVisibility(View.VISIBLE);
                }
            }
        });



        // -------------------------
        // -------------------------


        // -----------------------------------
        // Load suggested users from the server
        // -----------------------------------
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("/users/getUsers"))
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(loginInfo.getString("token", "null")))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject individualUser = data.getJSONObject(i);
                                suggestUsersList.add(new Profile_Model_minimal(
                                        individualUser.getString("userid"),
                                        individualUser.getString("username"),
                                        individualUser.getString("profilepic"),
                                        individualUser.getInt("isfollowedBy")
                                ));
                            }
                        } catch (JSONException e) {
                            Log.d("jsonException", "onResponse: ".concat(e.toString()));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("networkcallException", "onResponse: ".concat(anError.toString()));
                    }
                });

        // ----------------------
        // Setup posts RecyclerView
        // ----------------------
        posts = new ArrayList<>();
        ImagePostsFeedAdapter postsFeedAdapter = new ImagePostsFeedAdapter(requireActivity(), posts, suggestUsersList);
        LinearLayoutManager postsLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);

        postsFeedAdapter.setHasStableIds(true);
        mainXml.postsRecyclerView.setLayoutManager(postsLayoutManager);
        mainXml.postsRecyclerView.setNestedScrollingEnabled(true);
        mainXml.postsRecyclerView.setAdapter(postsFeedAdapter);

        // ----------------------------
        // just load a few video feed for reels
        videoPostsFeedViewModel.loadVideoPostFeed();

        // Observe LiveData from ViewModel
        // ----------------------------
        postsViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Posts_Model>>() {
            @Override
            public void onChanged(ArrayList<Posts_Model> posts_liveData) {

                if (posts_liveData != null && !posts_liveData.isEmpty()) {
//                    Log.d("homefragmentobserver", "onChanged: " + posts_liveData.toString());
                    posts.clear();
                    posts.addAll(posts_liveData);
                    postsFeedAdapter.notifyDataSetChanged();
                    mainXml.swipeRefresh.setRefreshing(false);
                    mainXml.swipeRefresh.setEnabled(true);
                    // Hide shimmer and show content
                    mainXml.shimmerView.stopShimmer();
                    mainXml.shimmerView.setVisibility(View.GONE);
                    mainXml.postsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    // Show shimmer if no data
                    mainXml.shimmerView.setVisibility(View.VISIBLE);
                    mainXml.shimmerView.startShimmer();
                }
            }
        });

        // ---------------------------------
        // Open AddPostActivity on button tap
        // ---------------------------------
        mainXml.addPostImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().startActivity(new Intent(getContext(), AddStoryActivity.class).putExtra("title","New Story"));
            }
        });
        mainXml.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainXml.swipeRefresh.setEnabled(false);
                postsViewModel.loadFeedPosts();
                storiesViewModel.loadStories();


            }
        });

        return mainXml.getRoot();
    }


    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();  // Stop video playback when fragment is paused
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
