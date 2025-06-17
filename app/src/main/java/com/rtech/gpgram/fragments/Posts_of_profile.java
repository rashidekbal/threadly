package com.rtech.gpgram.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.GridPostAdapter;
import com.rtech.gpgram.models.SearchpagePost_data_structure_base;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Posts_of_profile extends Fragment {
RecyclerView posts_all_recycler_view;
ShimmerFrameLayout shimmer_posts;
SharedPreferences loginInfo;
String baseUrl= BuildConfig.BASE_URL;
TextView NoPost_text;
ArrayList<SearchpagePost_data_structure_base> dataList=new ArrayList<>();
    StaggeredGridLayoutManager layoutManager;
    GridPostAdapter adapter;
    Post_fragmentSetCallback callback;

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
        layoutManager =new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        adapter=new GridPostAdapter(v.getContext(), dataList, new Post_fragmentSetCallback() {
            @Override
            public void openPostFragment(String url, int postid) {
                callback.openPostFragment(url, postid);


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
        getPostsOfProfile(v);



    }
    private void getPostsOfProfile(View v) {
        String getPostsUrl=baseUrl.concat("/posts/getUserPosts/").concat(loginInfo.getString("userid","null"));
        // Make network request to get posts
        AndroidNetworking.get(getPostsUrl)
                .setPriority(com.androidnetworking.common.Priority.HIGH)
                .addHeaders("Authorization", "Bearer ".concat(loginInfo.getString("token","null")))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener(){

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array= response.getJSONArray("data");

                            if(array.length()>0){
                                for(int i=0;i<array.length();i++){
                                    JSONObject post=array.getJSONObject(i);
                                    dataList.add(new SearchpagePost_data_structure_base(
                                            post.getInt("postid"),
                                            post.getString("imageurl")
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                                shimmer_posts.setVisibility(View.GONE);
                                posts_all_recycler_view.setVisibility(View.VISIBLE);
                            }else{
                                shimmer_posts.setVisibility(View.GONE);
                                NoPost_text.setVisibility(View.VISIBLE);
                                posts_all_recycler_view.setVisibility(View.GONE);

                            }


                        } catch (JSONException e) {
                            Log.d("profileDataErr", e.getMessage());




                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("profileDataErr", anError.getMessage());

                    }
                });

    }
}