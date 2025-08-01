package com.rtech.threadly.fragments.storiesFragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentUploadStoryFinalBinding;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.workers.UploadStoriesWorker;

import java.io.File;
import java.io.IOException;

public class UploadStoryFinalFragment extends Fragment {
    FragmentUploadStoryFinalBinding mainXml;
    String filePath;
    String mediaType;
    String from;
    File file;
    AppCompatActivity activity;
    WorkRequest uploadRequest;
    Data data;


    public UploadStoryFinalFragment() {
        // Required empty public constructor
    }


    @UnstableApi
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentUploadStoryFinalBinding.inflate(inflater,container,false);
        getData();
        setOnclickListeners();

        return mainXml.getRoot();
    }

    private void setOnclickListeners() {
        mainXml.generalUploadBtn.setOnClickListener(v->{
            mainXml.generalUploadBtn.setEnabled(false);
            if(file.exists()){
                if(mediaType.equals("image")){
                    data=new Data.Builder()
                            .putString("type","image")
                            .putString("path",file.getAbsolutePath())
                            .build();
                    uploadRequest=new OneTimeWorkRequest.Builder(UploadStoriesWorker.class).setInputData(data).build();
                    Core.getWorkManager().enqueue(uploadRequest);
                    ReUsableFunctions.ShowToast("Uploading");
                    requireActivity().finish();

                }else{
                    data=new Data.Builder()
                            .putString("type","video")
                            .putString("path",file.getAbsolutePath())
                            .build();
                    uploadRequest=new OneTimeWorkRequest.Builder(UploadStoriesWorker.class).setInputData(data).build();
                    Core.getWorkManager().enqueue(uploadRequest);
                    ReUsableFunctions.ShowToast("Uploading");
                    requireActivity().finish();

                }


            }else {
                ReUsableFunctions.ShowToast(activity,"no image found err");
                requireActivity().onBackPressed();
            }
        });
    }


    @UnstableApi
    private void getData(){
        Bundle bundle = getArguments();
        filePath= bundle.getString("filePath");
        mediaType = bundle.getString("mediaType");
        from = bundle.getString("from");

        if(from!=null && from.equals("camera")){
            file =new File(filePath);
        }else{
            try {
                file= ReUsableFunctions.getFileFromUri(requireContext(), Uri.parse(filePath));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

        if(mediaType.equals("video")){
            mainXml.imageView.setVisibility(View.GONE);
            mainXml.videoPlayerView.setVisibility(View.VISIBLE);
            ExoplayerUtil.stop();
            mainXml.videoPlayerView.setPlayer(null);
            ExoplayerUtil.play(file,mainXml.videoPlayerView);
        }else{
            mainXml.imageView.setVisibility(View.VISIBLE);
            mainXml.videoPlayerView.setVisibility(View.GONE);
            Glide.with(requireContext()).load(file).into(mainXml.imageView);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }


}