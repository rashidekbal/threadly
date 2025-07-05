package com.rtech.gpgram.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.R;
import com.rtech.gpgram.activities.AddPostActivity;
import com.rtech.gpgram.adapters.ImagePostsAdapter;
import com.rtech.gpgram.adapters.StatusViewAdapter;
import com.rtech.gpgram.models.Posts_Model;
import com.rtech.gpgram.models.Profile_Model_minimal;
import com.rtech.gpgram.viewmodels.PostsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class homeFragment extends Fragment {
    RecyclerView statusrecyclerView, postsRecyclerView;
    String api = BuildConfig.BASE_URL.concat("/posts/getPostsFeed");
    SharedPreferences loginInfo;
    ArrayList<Posts_Model> posts;
    ImageView addPost;
    ShimmerFrameLayout shimmerFrameLayout;
    ArrayList<Profile_Model_minimal> suggestUsersList = new ArrayList<>();
    private PostsViewModel postsViewModel;

    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);

        ///  this section is for status or you can say stories
        ArrayList<String> status = new ArrayList<>();
        status.add("a");
        status.add("b");
        status.add("a");
        status.add("b");
        status.add("a");
        status.add("b");
        status.add("a");
        status.add("b");
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        StatusViewAdapter adapter = new StatusViewAdapter(view.getContext(), status);
        statusrecyclerView.setLayoutManager(layoutManager);
        statusrecyclerView.setAdapter(adapter);
        ///  status part ends here

        /// load suggested users
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("/users/getUsers"))
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(loginInfo.getString("token", "null")))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject individualUser = data.getJSONObject(i);
                                suggestUsersList.add(
                                        new Profile_Model_minimal(
                                                individualUser.getString("userid")
                                                , individualUser.getString("username")
                                                , individualUser.getString("profilepic")
                                                , individualUser.getInt("isfollowedBy")));
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


        ///  posts section starts here

        posts = new ArrayList<Posts_Model>();
        ImagePostsAdapter imagePostsAdapter = new ImagePostsAdapter(view.getContext(), posts, loginInfo, suggestUsersList);
        LinearLayoutManager postsLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        imagePostsAdapter.setHasStableIds(true);
        postsRecyclerView.setLayoutManager(postsLayoutManager);
        postsRecyclerView.setNestedScrollingEnabled(true);
        postsRecyclerView.setAdapter(imagePostsAdapter);
        postsRecyclerView.setNestedScrollingEnabled(true);


        postsViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Posts_Model>>() {
            @Override
            public void onChanged(ArrayList<Posts_Model> posts_liveData) {
                if (posts_liveData != null && !posts_liveData.isEmpty()) {
                    Log.d("homefragmentobserver", "onChanged: " + posts_liveData.toString());
                    posts.addAll(posts_liveData);
                    imagePostsAdapter.notifyDataSetChanged();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    postsRecyclerView.setVisibility(View.VISIBLE);


                } else {
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmer();


                }
            }
        });


        ///  aad post screen opener
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), AddPostActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onresumeHome", posts.toString());
    }

    private void init(View v) {
        statusrecyclerView = v.findViewById(R.id.Status_recycler_view);
        postsRecyclerView = v.findViewById(R.id.posts_recyclerView);
        AndroidNetworking.initialize(v.getContext());
        addPost = v.findViewById(R.id.add_post_image_btn);
        loginInfo = requireActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        shimmerFrameLayout = v.findViewById(R.id.shimmerView);
        postsViewModel = new ViewModelProvider(requireActivity()).get(PostsViewModel.class);
    }
}