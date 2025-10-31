package com.rtech.threadly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.adapters.postsAdapters.GridPostAdapter;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.Preview_Post_model;
import com.rtech.threadly.interfaces.Post_fragmentSetCallback;

import java.util.ArrayList;

public class searchFragment extends Fragment {
RecyclerView search_page_recycler_view;
ArrayList<Posts_Model> dataList;
GridPostAdapter adapter;

    public searchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        dataList=new ArrayList<>();

        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        adapter =new GridPostAdapter(view.getContext(), dataList, new Post_fragmentSetCallback() {


            @Override
            public void openPostFragment(ArrayList<Posts_Model> postsArray, int position) {
                ///  this will be used to create a feed from the selected search post in future
            }

            @Override
            public void openEditor() {
                //no uses
            }
        });
        adapter.setHasStableIds(true);
        search_page_recycler_view.setLayoutManager(layoutManager);
        search_page_recycler_view.setAdapter(adapter);

        return view;
    }




    public void init(View v){
        search_page_recycler_view=v.findViewById(R.id.search_page_recycler_view);

    }
}