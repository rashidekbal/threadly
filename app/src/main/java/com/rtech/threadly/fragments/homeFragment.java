package com.rtech.threadly.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.R;
import com.rtech.threadly.activities.AddPostActivity;
import com.rtech.threadly.adapters.PostsFeedAdapter;
import com.rtech.threadly.adapters.StatusViewAdapter;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentHomeBinding;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.Profile_Model_minimal;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.viewmodels.PostsViewModel;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class homeFragment extends Fragment {

    SharedPreferences loginInfo;
    ArrayList<Posts_Model> posts;
    ArrayList<Profile_Model_minimal> suggestUsersList = new ArrayList<>();
    private PostsViewModel postsViewModel;
    FragmentHomeBinding mainXml;

    public homeFragment() {
        // Default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout using ViewBinding
        mainXml = FragmentHomeBinding.inflate(inflater, container, false);
        postsViewModel = new ViewModelProvider(requireActivity()).get(PostsViewModel.class);
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
        PostsFeedAdapter postsFeedAdapter = new PostsFeedAdapter(requireActivity(), posts, suggestUsersList);
        LinearLayoutManager postsLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);

        postsFeedAdapter.setHasStableIds(true);
        mainXml.postsRecyclerView.setLayoutManager(postsLayoutManager);
        mainXml.postsRecyclerView.setNestedScrollingEnabled(true);
        mainXml.postsRecyclerView.setAdapter(postsFeedAdapter);

        // ----------------------------
        // Observe LiveData from ViewModel
        // ----------------------------
        postsViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Posts_Model>>() {
            @Override
            public void onChanged(ArrayList<Posts_Model> posts_liveData) {
                if (posts_liveData != null && !posts_liveData.isEmpty()) {
                    Log.d("homefragmentobserver", "onChanged: " + posts_liveData.toString());

                    posts.addAll(posts_liveData);
                    postsFeedAdapter.notifyDataSetChanged();

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

        // ----------------------------------------------------
        // Scroll listener to detect center post and autoplay
        // ----------------------------------------------------
        mainXml.postsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null) return;

                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();

                    int centerPosition = -1;
                    int recyclerViewCenterY = recyclerView.getHeight() / 2;
                    int closestDistance = Integer.MAX_VALUE;

                    // Find the item closest to the center of the screen
                    for (int i = firstVisible; i <= lastVisible; i++) {
                        View child = layoutManager.findViewByPosition(i);
                        if (child == null) continue;

                        int childCenterY = (child.getTop() + child.getBottom()) / 2;
                        int distance = Math.abs(childCenterY - recyclerViewCenterY);

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            centerPosition = i;
                        }
                    }

                    // If new center position is different from currently playing one, update and notify
                    if (centerPosition != -1 && postsFeedAdapter.PlayingPosition != centerPosition) {
                        Log.d("ScrollListener", "Center position to play = " + centerPosition);
                        postsFeedAdapter.PlayingPosition = centerPosition;
                        postsFeedAdapter.notifyDataSetChanged();
                    }
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
                mainXml.swipeRefresh.setRefreshing(false);
                mainXml.swipeRefresh.setEnabled(true);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExoplayerUtil.release();  // Clean up ExoPlayer resources
    }
}
