package com.rtech.threadly.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.activities.Messenger.MessengerMainMessagePageActivity;
import com.rtech.threadly.adapters.messanger.NewMsgListAdapter;
import com.rtech.threadly.databinding.FragmentUsersListBinding;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

import java.util.ArrayList;

public class UsersListFragment extends Fragment {
    FragmentUsersListBinding mainXml;
    MessageAbleUsersViewModel usersViewModel;
    ArrayList <UsersModel> usersList;
    LinearLayoutManager layoutManager;
    NewMsgListAdapter adapter;

OnDestroyFragmentCallback callback;
    public UsersListFragment() {
        // Required empty public constructor
    }
    public UsersListFragment(OnDestroyFragmentCallback callback){
this.callback=callback;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentUsersListBinding.inflate(inflater,container,false);
        init();
        return mainXml.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        usersList=new ArrayList<>();
        layoutManager=new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        adapter=new NewMsgListAdapter(requireActivity(), model -> {
            Bundle data=new Bundle();
            data.putString("userid",model.getUserId());
            data.putString("username",model.getUsername());
            data.putString("profilePic",model.getProfilePic());
            data.putString("uuid",model.getUuid());
            Intent msgPage=new Intent(requireActivity(), MessengerMainMessagePageActivity.class);
            msgPage.putExtras(data);
            requireActivity().startActivity(msgPage);
            requireActivity().getSupportFragmentManager().popBackStack();

        }, usersList);
        mainXml.RecyclerView.setLayoutManager(layoutManager);
        mainXml.RecyclerView.setAdapter(adapter);

        usersViewModel=new ViewModelProvider(requireActivity()).get(MessageAbleUsersViewModel.class);
        usersViewModel.getUsersList().observe(getViewLifecycleOwner(), usersModels -> {

           if(!usersModels.isEmpty()){
               usersList.clear();
               usersList.addAll(usersModels);
               adapter.notifyDataSetChanged();

           }
        });

    }

    @Override
    public void onDestroy() {
        callback.onDestroy();
        super.onDestroy();
    }
}