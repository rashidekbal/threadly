package com.rtech.threadly.fragments.follower_following_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rtech.threadly.R;
import com.rtech.threadly.adapters.followersAdapters.FollowerFollowing_UserList_adapter;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.network_managers.FollowManager;
import com.rtech.threadly.models.Profile_Model_minimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Followings extends Fragment {
    String userId;
    FollowManager followManager;
    ArrayList<Profile_Model_minimal> dataList = new ArrayList<>();
    FollowerFollowing_UserList_adapter adapter;
    LinearLayoutManager layoutManager;
    RecyclerView followings_recycler_view;
    RelativeLayout noData_relativeLayout,loadingData_relativeLayout;



    public Followings() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_followings, container, false);
        init(v);
        loadData();
        return v;
    }

    private void init(View v) {
        followings_recycler_view=v.findViewById(R.id.followings_recycler_view);
        noData_relativeLayout=v.findViewById(R.id.noData_relativeLayout);
        loadingData_relativeLayout=v.findViewById(R.id.loadingData_relativeLayout);
        userId = getArguments().getString("userid");
        followManager = new FollowManager();
        adapter = new FollowerFollowing_UserList_adapter(v.getContext(), dataList);
        layoutManager=new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false);
        followings_recycler_view.setLayoutManager(layoutManager);
        followings_recycler_view.setAdapter(adapter);

    }


    private void loadData(){
        loadingData_relativeLayout.setVisibility(View.VISIBLE);
        noData_relativeLayout.setVisibility(View.GONE);
        followManager.getFollowings(userId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                followings_recycler_view.setVisibility(View.VISIBLE);
                loadingData_relativeLayout.setVisibility(View.GONE);
                try {
                    JSONArray followingsArray = response.getJSONArray("data");
                    if(followingsArray.length()>0){
                    for (int i = 0; i< followingsArray.length(); i++){
                        JSONObject followerObject = followingsArray.getJSONObject(i);
                        dataList.add(new Profile_Model_minimal(followerObject.getString("userid")
                                ,followerObject.getString("username"),
                                followerObject.getString("profilepic"),
                                followerObject.getInt("ifFollowed")));
                    }
                    adapter.notifyDataSetChanged();}else{
                        loadingData_relativeLayout.setVisibility(View.GONE);
                        noData_relativeLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onError(String err) {
                loadingData_relativeLayout.setVisibility(View.GONE);
                noData_relativeLayout.setVisibility(View.VISIBLE);


            }
        });
    }
}