package com.rtech.threadly.fragments.profileFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.models.Preview_Post_model;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import java.util.ArrayList;

public class Posts_of_profile extends Fragment {
RecyclerView posts_all_recycler_view;
ShimmerFrameLayout shimmer_posts;
SharedPreferences loginInfo;
String baseUrl= BuildConfig.BASE_URL;
TextView NoPost_text;
ArrayList<Preview_Post_model> dataList=new ArrayList<>();
    GridLayoutManager layoutManager;
    GridPostAdapter adapter;
    Post_fragmentSetCallback callback;
    ProfileViewModel profileViewModel;

    public Posts_of_profile(Post_fragmentSetCallback callback) {
   this.callback=callback;
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
        profileViewModel.getUserPostsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Preview_Post_model>>() {
            @Override
            public void onChanged(ArrayList<Preview_Post_model> postsData) {
                if(postsData.size()>0){
                    dataList.clear();
                    dataList.addAll(postsData);
                    adapter.notifyDataSetChanged();
                    shimmer_posts.setVisibility(View.GONE);
                    posts_all_recycler_view.setVisibility(View.VISIBLE);
                    NoPost_text.setVisibility(View.GONE);
                }else{
                    shimmer_posts.setVisibility(View.GONE);
                    NoPost_text.setVisibility(View.VISIBLE);
                    posts_all_recycler_view.setVisibility(View.GONE);
                }

            }
        });
        layoutManager =new GridLayoutManager(requireActivity(),3 ,GridLayoutManager.VERTICAL,false);
        adapter=new GridPostAdapter(v.getContext(), dataList, new Post_fragmentSetCallback() {
            @Override
            public void openPostFragment(String url, int postid) {
                callback.openPostFragment(url, postid);


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




    }

}