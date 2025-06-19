package com.rtech.gpgram.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.GridPostAdapter;
import com.rtech.gpgram.models.Preview_Post_model;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;

import java.util.ArrayList;

public class searchFragment extends Fragment {
RecyclerView search_page_recycler_view;
ArrayList<Preview_Post_model> dataList;
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
        dataList.add(new Preview_Post_model(1,"https://media.istockphoto.com/id/1315856341/photo/young-woman-meditating-outdoors-at-park.webp"));
        dataList.add(new Preview_Post_model(2,"https://cdn.pixabay.com/photo/2025/05/11/00/44/flowers-9591978_1280.jpg"));
        dataList.add(new Preview_Post_model(3,"https://cdn.pixabay.com/photo/2025/05/13/17/05/dove-9597944_1280.jpg"));
        dataList.add(new Preview_Post_model(4,"https://cdn.pixabay.com/photo/2025/05/12/06/36/dragon-fly-9594679_1280.jpg"));
        dataList.add(new Preview_Post_model(5,"https://cdn.pixabay.com/photo/2025/05/12/18/01/tit-9595802_1280.jpg"));
        dataList.add(new Preview_Post_model(6,"https://cdn.pixabay.com/photo/2025/05/13/12/20/apples-9597475_1280.jpg"));
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        adapter =new GridPostAdapter(view.getContext(), dataList, new Post_fragmentSetCallback() {
            @Override
            public void openPostFragment(String url,int postid) {


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