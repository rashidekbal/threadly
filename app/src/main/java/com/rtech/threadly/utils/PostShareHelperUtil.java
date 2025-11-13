package com.rtech.threadly.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.adapters.messanger.UsersShareSheetGridAdapter;
import com.rtech.threadly.constants.TypeConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.models.Posts_Model;
import com.rtech.threadly.models.UsersModel;
import com.rtech.threadly.viewmodels.MessageAbleUsersViewModel;

import org.json.JSONException;

import java.util.ArrayList;

public class PostShareHelperUtil {
    public static void OpenPostShareDialog(Posts_Model post, Context context){
        ArrayList<UsersModel> selectedUsers=new ArrayList<>();
        BottomSheetDialog shareBottomSheet=new BottomSheetDialog(context, R.style.TransparentBottomSheet);
        shareBottomSheet.setContentView(R.layout.post_share_layout);
        AppCompatButton sendBtn=shareBottomSheet.findViewById(R.id.sendBtn);
        RelativeLayout actionButtons_rl=shareBottomSheet.findViewById(R.id.actionButtons_rl);
        ImageView search_btn=shareBottomSheet.findViewById(R.id.search_btn);
        EditText search_edit_text=shareBottomSheet.findViewById(R.id.search_edit_text);
        ImageView suggestUsersBtn=shareBottomSheet.findViewById(R.id.suggestUsersBtn);
        RecyclerView Users_List_recyclerView=shareBottomSheet.findViewById(R.id.Users_List_recyclerView);
        LinearLayout Story_add_ll_btn=shareBottomSheet.findViewById(R.id.Story_add_ll_btn);
        ProgressBar progressBar=shareBottomSheet.findViewById(R.id.progressBar);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,3);
        assert Users_List_recyclerView != null;
        Users_List_recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<UsersModel> usersModelList=new ArrayList<>();
        UsersShareSheetGridAdapter adapter=new UsersShareSheetGridAdapter(context, usersModelList, model -> {
            if(selectedUsers.contains(model)){
                selectedUsers.remove(model);
            }else{
                selectedUsers.add(model);
            }
            assert actionButtons_rl != null;
            if(selectedUsers.isEmpty()){
                actionButtons_rl.setVisibility(View.VISIBLE);
                assert sendBtn != null;
                sendBtn.setVisibility(View.GONE);

            }else{
                actionButtons_rl.setVisibility(View.GONE);
                assert sendBtn != null;
                sendBtn.setVisibility(View.VISIBLE);
            }

        });
        Users_List_recyclerView.setAdapter(adapter);



        MessageAbleUsersViewModel messageAbleUsersViewModel=new ViewModelProvider((AppCompatActivity)context).get(MessageAbleUsersViewModel.class);
        messageAbleUsersViewModel.getUsersList().observe((AppCompatActivity) context, usersModels -> {
            if(usersModels.isEmpty()){
                Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show();

            }else{
                usersModelList.clear();
                usersModelList.addAll(usersModels);
                adapter.notifyDataSetChanged();

            }
            assert progressBar != null;
            progressBar.setVisibility(View.GONE);

        });

        //send btn action
        assert sendBtn != null;
        sendBtn.setOnClickListener(v->{
            int postid=post.postId;
            if(!selectedUsers.isEmpty()){
                for(UsersModel model:selectedUsers){
                    try {
                        ReUsableFunctions.ShowToast(" "+postid);
                        Core.sendCtoS(model.getUuid(),"", TypeConstants.POST,post.postUrl,postid,"sent a reel by "+post.username);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                selectedUsers.clear();
                sendBtn.setVisibility(View.GONE);
                assert actionButtons_rl != null;
                actionButtons_rl.setVisibility(View.VISIBLE);

            }
            shareBottomSheet.dismiss();
        });

        shareBottomSheet.show();

    }
}
