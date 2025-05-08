package com.rtech.gpgram.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.StatusViewAdapter;

import java.util.ArrayList;


public class homeFragment extends Fragment {
    RecyclerView statusrecyclerView;

    public homeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);
        ArrayList<String> list=new ArrayList<>();
        list.add("hello");
        list.add("hi");
        list.add("hello");
        list.add("hi");
        list.add("hello");
        list.add("hi");
        StatusViewAdapter adapter=new StatusViewAdapter(view.getContext(),list);
        statusrecyclerView.setLayoutManager(layoutManager);
        statusrecyclerView.setAdapter(adapter);




        return view;
    }

    private void init(View v){
        statusrecyclerView=v.findViewById(R.id.Status_recycler_view);
    }
}