package com.rtech.gpgram.fragments.follower_following_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.FollowerFollowing_UserList_adapter;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.managers.FollowManager;
import com.rtech.gpgram.models.Profile_Model_minimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Followers extends Fragment {
    String userId;
    FollowManager followManager;
    ArrayList<Profile_Model_minimal> dataList = new ArrayList<>();
    FollowerFollowing_UserList_adapter adapter;
    LinearLayoutManager layoutManager;
    RecyclerView followers_recycler_view;
    RelativeLayout noData_relativeLayout,loadingData_relativeLayout;




    public Followers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_followers, container, false);
        init(v);
        loadData();




        return v;
    }
    private void init(View v) {
            followers_recycler_view=v.findViewById(R.id.followers_recycler_view);
            noData_relativeLayout=v.findViewById(R.id.noData_relativeLayout);
            loadingData_relativeLayout=v.findViewById(R.id.loadingData_relativeLayout);
            userId = getArguments().getString("userid");
            followManager = new FollowManager(v.getContext());
            adapter = new FollowerFollowing_UserList_adapter(v.getContext(), dataList);
            layoutManager=new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false);
            followers_recycler_view.setLayoutManager(layoutManager);
            followers_recycler_view.setAdapter(adapter);

    }



    
    private void loadData(){
        loadingData_relativeLayout.setVisibility(View.VISIBLE);
        noData_relativeLayout.setVisibility(View.GONE);
        followManager.getFollowers(userId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
            
                followers_recycler_view.setVisibility(View.VISIBLE);
                loadingData_relativeLayout.setVisibility(View.GONE);
                try {
                    JSONArray followersArray = response.getJSONArray("data");
                    if(followersArray.length()>0){
                    for (int i=0;i<followersArray.length();i++){
                        JSONObject followerObject = followersArray.getJSONObject(i);
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