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
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.activities.AddPostActivity;
import com.rtech.threadly.adapters.ImagePostsFeedAdapter;
import com.rtech.threadly.adapters.StatusViewAdapter;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentHomeBinding;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.viewmodels.ImagePostsFeedViewModel;
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


    public homeFragment() {
        // Default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout using ViewBinding
        mainXml = FragmentHomeBinding.inflate(inflater, container, false);
        postsViewModel = new ViewModelProvider(requireActivity()).get(ImagePostsFeedViewModel.class);
        videoPostsFeedViewModel=new ViewModelProvider(requireActivity()).get(VideoPostsFeedViewModel.class);
        loginInfo = Core.getPreference();

        // -------------------------
        // Setup fake stories/status
        // -------------------------
        ArrayList<String> status = new ArrayList<>();
        status.add("a"); status.add("b");
        status.add("a"); status.add("b");
        status.add("a"); status.add("b");
        status.add("a"); status.add("b");

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        StatusViewAdapter adapter = new StatusViewAdapter(requireActivity(), status);
        mainXml.storyRecyclerView.setLayoutManager(layoutManager);
        mainXml.storyRecyclerView.setAdapter(adapter);

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
                    Log.d("homefragmentobserver", "onChanged: " + posts_liveData.toString());
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
                getContext().startActivity(new Intent(getContext(), AddPostActivity.class));
            }
        });
        mainXml.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainXml.swipeRefresh.setEnabled(false);
                postsViewModel.loadFeedPosts();


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
