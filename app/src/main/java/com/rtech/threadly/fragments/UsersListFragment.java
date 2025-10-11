package com.rtech.threadly.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.activities.Messanger.MessengerMainMessagePageActivity;
import com.rtech.threadly.adapters.messanger.NewMsgListAdapter;
import com.rtech.threadly.databinding.FragmentUsersListBinding;
import com.rtech.threadly.interfaces.Messanger.OnUserSelectedListener;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.viewmodels.MessaageAbleUsersViewModel;

import java.util.ArrayList;

public class UsersListFragment extends Fragment {
    FragmentUsersListBinding mainXml;
    MessaageAbleUsersViewModel usersViewModel;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentUsersListBinding.inflate(inflater,container,false);
        init();
        return mainXml.getRoot();
    }

    private void init() {
        usersList=new ArrayList<>();
        layoutManager=new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        adapter=new NewMsgListAdapter(requireActivity(), new OnUserSelectedListener() {
            @Override
            public void onSelect(UsersModel model) {
                Bundle data=new Bundle();
                data.putString("userid",model.getUserId());
                data.putString("username",model.getUsername());
                data.putString("profilePic",model.getProfilePic());
                data.putString("uuid",model.getUuid());
                Intent msgPage=new Intent(requireActivity(), MessengerMainMessagePageActivity.class);
                msgPage.putExtras(data);
                requireActivity().startActivity(msgPage);
                requireActivity().getSupportFragmentManager().popBackStack();

            }
        }, usersList);
        mainXml.RecyclerView.setLayoutManager(layoutManager);
        mainXml.RecyclerView.setAdapter(adapter);

        usersViewModel=new ViewModelProvider(requireActivity()).get(MessaageAbleUsersViewModel.class);
        usersViewModel.getUsersList().observe(getViewLifecycleOwner(), new Observer<ArrayList<UsersModel>>() {
            @Override
            public void onChanged(ArrayList<UsersModel> usersModels) {

               if(!usersModels.isEmpty()){
                   usersList.clear();
                   usersList.addAll(usersModels);
                   adapter.notifyDataSetChanged();

               }
            }
        });

    }

    @Override
    public void onDestroy() {
        callback.onDestroy();
        super.onDestroy();
    }
}