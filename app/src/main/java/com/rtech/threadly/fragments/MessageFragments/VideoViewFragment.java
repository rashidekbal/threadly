package com.rtech.threadly.fragments.MessageFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentVideoViewBinding;
import com.rtech.threadly.utils.DownloadManagerUtil;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;


public class VideoViewFragment extends Fragment {
    FragmentVideoViewBinding mainXml;
    Bundle data;
    String username,userid,profileUrl,mediaUrl;
    String messageUid;
    public VideoViewFragment(){
        // Required empty public constructor
    }


    @OptIn(markerClass = UnstableApi.class)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentVideoViewBinding.inflate(inflater,container,false);
          init();

        return mainXml.getRoot();
    }
    private void init(){
        data=getArguments();
        if(data!=null){
            username=data.getString("username");
            userid=data.getString("userid");
            profileUrl=data.getString("profileUrl");
            mediaUrl=data.getString("mediaUrl");
            messageUid=data.getString("messageUid");

        }else{
            Toast.makeText(requireActivity(), "something went Wrong..", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        setUserdata();
        mainXml.optionDots.setOnClickListener(v->{
            showOptionsSheet();
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setUserdata() {
        mainXml.username.setText(username);
        mainXml.userId.setText(userid);
        Glide.with(requireActivity()).load(profileUrl).placeholder(R.drawable.blank_profile).into(mainXml.profile);
        ExoplayerUtil.playNoLoop(Uri.parse(mediaUrl),mainXml.videoPlayerView);

    }

    private void showOptionsSheet(){
        BottomSheetDialog optionsDialog=new BottomSheetDialog(requireActivity());
        optionsDialog.setContentView(R.layout.message_media_fargment_options_layout);
        FrameLayout bottomSheetFrame=optionsDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        optionsDialog.setCancelable(true);
        if(bottomSheetFrame!=null){
            BottomSheetBehavior<FrameLayout> behavior=BottomSheetBehavior.from(bottomSheetFrame);
            behavior.setDraggable(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }
        setOnclickListeners_optionSheet(optionsDialog);
        optionsDialog.show();

    }

    private void setOnclickListeners_optionSheet(BottomSheetDialog optionsDialog) {
        LinearLayout downloadBtn=optionsDialog.findViewById(R.id.download_btn);
        LinearLayout deleteBtn=optionsDialog.findViewById(R.id.delete_btn);
        downloadBtn.setOnClickListener(v->{
            new AlertDialog.Builder(requireActivity())
                    .setTitle("download")
                    .setMessage("do you want to save this media")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ReUsableFunctions.ShowToast("saving....");
                            DownloadManagerUtil.downloadFromUri(requireActivity(),Uri.parse(mediaUrl));
                            dialog.dismiss();
                            optionsDialog.dismiss();

                        }
                    })
                    .setNegativeButton("no thanks ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            optionsDialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();

        });
        deleteBtn.setOnClickListener(v->{
            new AlertDialog.Builder(requireActivity())
                    .setTitle("delete")
                    .setMessage("do you want delete this media, this can't be undone")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ReUsableFunctions.DeleteMessage(messageUid);
                            dialog.dismiss();
                            optionsDialog.dismiss();
                            requireActivity().getSupportFragmentManager().popBackStack();

                        }
                    })
                    .setNegativeButton("no thanks ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            optionsDialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();

        });
    }

    @Override
    public void onDestroy() {
        ExoplayerUtil.stop();
        super.onDestroy();

    }
}