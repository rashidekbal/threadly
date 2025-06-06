package com.rtech.gpgram.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.SearchPagePostAdapter;
import com.rtech.gpgram.structures.SearchpagePost_data_structure_base;

import java.util.ArrayList;

public class Posts_of_profile extends Fragment {
RecyclerView posts_all_recycler_view;

    public Posts_of_profile() {
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
        ArrayList<SearchpagePost_data_structure_base> dataList=new ArrayList<>();
        dataList.add(new SearchpagePost_data_structure_base(1,"https://media.istockphoto.com/id/1315856341/photo/young-woman-meditating-outdoors-at-park.webp"));
        dataList.add(new SearchpagePost_data_structure_base(2,"https://cdn.pixabay.com/photo/2025/05/11/00/44/flowers-9591978_1280.jpg"));
        dataList.add(new SearchpagePost_data_structure_base(3,"https://cdn.pixabay.com/photo/2025/05/13/17/05/dove-9597944_1280.jpg"));
        dataList.add(new SearchpagePost_data_structure_base(4,"https://cdn.pixabay.com/photo/2025/05/12/06/36/dragon-fly-9594679_1280.jpg"));
        dataList.add(new SearchpagePost_data_structure_base(5,"https://cdn.pixabay.com/photo/2025/05/12/18/01/tit-9595802_1280.jpg"));
        dataList.add(new SearchpagePost_data_structure_base(6,"https://cdn.pixabay.com/photo/2025/05/13/12/20/apples-9597475_1280.jpg"));
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        SearchPagePostAdapter adapter =new SearchPagePostAdapter(v.getContext(),dataList);
        adapter.setHasStableIds(true);
        posts_all_recycler_view.setLayoutManager(layoutManager);
        posts_all_recycler_view.setAdapter(adapter);


        return v;
    }
    public void init(View v){
        posts_all_recycler_view=v.findViewById(R.id.posts_all_recycler_view);

    }
}