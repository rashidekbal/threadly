package com.rtech.threadly.fragments.profileFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import java.util.ArrayList;

public class Posts_of_profile extends Fragment {
RecyclerView posts_all_recycler_view;
ShimmerFrameLayout shimmer_posts;
SharedPreferences loginInfo;
String baseUrl= BuildConfig.BASE_URL;
TextView NoPost_text;
ArrayList<Posts_Model> postsArray =new ArrayList<>();
    GridLayoutManager layoutManager;
    GridPostAdapter adapter;
    Post_fragmentSetCallback callback;
    ProfileViewModel profileViewModel;
    private final int threshold =20;

    public Posts_of_profile(Post_fragmentSetCallback callback) {
   this.callback=callback;
    }
    public Posts_of_profile(){
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View v=  inflater.inflate(R.layout.fragment_posts_of_profile, container, false);
        init(v);
        profileViewModel.getUserPostsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Posts_Model>>() {
            @Override
            public void onChanged(ArrayList<Posts_Model> postsData) {
                if(postsData.isEmpty()){
                    shimmer_posts.setVisibility(View.GONE);
                    NoPost_text.setVisibility(View.VISIBLE);
                    posts_all_recycler_view.setVisibility(View.GONE);
                }
                else {
                    postsArray.clear();
                    postsArray.addAll(postsData);
                    shimmer_posts.setVisibility(View.GONE);
                    posts_all_recycler_view.setVisibility(View.VISIBLE);
                    NoPost_text.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();


                }
            }
        });
        layoutManager =new GridLayoutManager(requireActivity(),3 ,GridLayoutManager.VERTICAL,false);
        adapter=new GridPostAdapter(v.getContext(), postsArray, new Post_fragmentSetCallback() {


            @Override
            public void openPostFragment(ArrayList<Posts_Model> postsArray, int position) {
                callback.openPostFragment(postsArray,position);

            }

            @Override
            public void openEditor() {

            }
        });
        adapter.setHasStableIds(true);
        posts_all_recycler_view.setLayoutManager(layoutManager);
        posts_all_recycler_view.setAdapter(adapter);


        return v;
    }
    public void init(View v){
        posts_all_recycler_view=v.findViewById(R.id.posts_all_recycler_view);
        shimmer_posts=v.findViewById(R.id.shimmer_posts);
        loginInfo=v.getContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
        NoPost_text=v.findViewById(R.id.NoPost_text);
        profileViewModel=new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        posts_all_recycler_view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    int visibleItemCount=layoutManager.getChildCount();
                    int totalItemCount=layoutManager.getItemCount();
                    int FirstVisible=layoutManager.findFirstVisibleItemPosition();
                    if(visibleItemCount+FirstVisible>=totalItemCount-threshold){
                        profileViewModel.loadMorePosts();
                    }
                }



            }
        });

    }

}