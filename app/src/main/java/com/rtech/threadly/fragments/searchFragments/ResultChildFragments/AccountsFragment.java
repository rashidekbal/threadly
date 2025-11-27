package com.rtech.threadly.fragments.searchFragments.ResultChildFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.adapters.SearchPage.AccountsAdapter;
import com.rtech.threadly.databinding.FragmentAccountsBinding;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.SearchViewModel;

import java.util.ArrayList;

public class AccountsFragment extends Fragment {
    FragmentAccountsBinding mainXml;
    AccountsAdapter adapter;
    ArrayList<UsersModel> usersModelArrayList;
    SearchViewModel viewModel;


    public AccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentAccountsBinding.inflate(inflater,container,false);
        init();
        return mainXml.getRoot();
    }

    private void init() {
        viewModel=new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        usersModelArrayList=new ArrayList<>();
        adapter=new AccountsAdapter(usersModelArrayList,requireActivity());
        mainXml.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mainXml.recyclerView.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        viewModel.getAccountsResult().observe(requireActivity(),usersModels->{
            if(usersModels==null){
                mainXml.progressbar.setVisibility(View.VISIBLE);
                mainXml.recyclerView.setVisibility(View.GONE);
                mainXml.noDataText.setVisibility(View.GONE);
                return ;
            }
            mainXml.progressbar.setVisibility(View.GONE);
            if(!usersModels.isEmpty()){
                mainXml.recyclerView.setVisibility(View.VISIBLE);
                mainXml.noDataText.setVisibility(View.GONE);
                usersModelArrayList.clear();
                usersModelArrayList.addAll(usersModels);
                adapter.notifyDataSetChanged();
            }else{
                mainXml.recyclerView.setVisibility(View.GONE);
                mainXml.noDataText.setVisibility(View.VISIBLE);
            }
        });
    }
}