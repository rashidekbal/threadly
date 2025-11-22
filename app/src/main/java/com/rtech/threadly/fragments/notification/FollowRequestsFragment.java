package com.rtech.threadly.fragments.notification;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.adapters.followRequestsAdapter.FollowRequestsAdapter;
import com.rtech.threadly.databinding.FragmentFollowRequestsBinding;
import com.rtech.threadly.interfaces.NetworkCallBacks.NetworkCallbackInterfaceJsonObject;
import com.rtech.threadly.models.FollowRequestModel;
import com.rtech.threadly.network_managers.FollowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class FollowRequestsFragment extends Fragment {
FragmentFollowRequestsBinding mainXml;
ArrayList<FollowRequestModel> request;
FollowRequestsAdapter adapter;
    public FollowRequestsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainXml=FragmentFollowRequestsBinding.inflate(inflater,container,false);
        setUpFragment();
        return mainXml.getRoot();
    }
    private void setUpFragment(){
        request=new ArrayList<>();
        adapter=new FollowRequestsAdapter(requireActivity(),request);
        mainXml.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mainXml.requestsRecyclerView.setAdapter(adapter);
        mainXml.backBtn.setOnClickListener(v -> getActivity().onBackPressed());
        mainXml.progressBar.setVisibility(View.VISIBLE);
        loadFollowRequests();
    }
    private void loadFollowRequests(){
        FollowManager.getAllFollowRequests(new NetworkCallbackInterfaceJsonObject() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(JSONObject response) {
                mainXml.progressBar.setVisibility(View.GONE);
                JSONArray data=response.optJSONArray("data");
                assert data!=null;
                if(data.length()==0){mainXml.noRequestsText.setVisibility(View.VISIBLE);}
                ArrayList<FollowRequestModel> tempArray=new ArrayList<>();
                for (int i=0;i<data.length();i++){
                    JSONObject object=data.optJSONObject(i);
                    tempArray.add(new FollowRequestModel(object.optString("username"),
                            object.optString("userid"),
                            object.optString("profilepic"),
                            false,
                            ""));
                }
                mainXml.requestsRecyclerView.setVisibility(View.VISIBLE);
                request.clear();
                request.addAll(tempArray);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode) {
               mainXml.noRequestsText.setVisibility(View.VISIBLE);
               mainXml.progressBar.setVisibility(View.GONE);
            }
        });
    }
}