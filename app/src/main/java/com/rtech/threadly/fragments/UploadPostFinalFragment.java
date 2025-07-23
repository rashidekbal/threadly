package com.rtech.threadly.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.databinding.FragmentUploadPostFinalBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.PostUploadedCallback;
import com.rtech.threadly.managers.PostsManager;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.workers.UploadMediaWorker;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class UploadPostFinalFragment extends Fragment {
    FragmentUploadPostFinalBinding mainXml;
    String filePath;
    String mediaType;
    String from;
    File file;
    AppCompatActivity activity;
    WorkRequest uploadRequest;
    Data data;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentUploadPostFinalBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        activity=(AppCompatActivity) requireActivity();

            activity.setSupportActionBar(mainXml.toolbar);
            activity.getSupportActionBar().setTitle("New Post");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);

            getData();
            mainXml.imagePlaceHolder.setVisibility(View.GONE);
            mainXml.imageLoadProgressBar.setVisibility(View.GONE);
            mainXml.imageView.setVisibility(View.VISIBLE);
            Glide.with(requireContext())
                .load(file)
                .into(mainXml.imageView);
            setOnclickListeners();


        return mainXml.getRoot();
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.onBackPressed();
            return true;
        }
        return false;
    }
    private void getData(){
        Bundle bundle = getArguments();
        filePath= bundle.getString("filePath");
        mediaType = bundle.getString("mediaType");
        from = bundle.getString("from");

        if(from!=null && from.equals("camera")){
            file =new File(filePath);
        }else{
            try {
                file=ReUsableFunctions.getFileFromUri(requireContext(),Uri.parse(filePath));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }
    private void setOnclickListeners() {
        mainXml.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainXml.shareBtn.setEnabled(false);
                if(file.exists()){
                    String caption=mainXml.captionText.getText().toString().trim();
                    if(mediaType.equals("image")){
                        data=new Data.Builder()
                                .putString("type","image")
                                .putString("path",file.getAbsolutePath())
                                .putString("caption",caption)
                                .build();
                        uploadRequest=new OneTimeWorkRequest.Builder(UploadMediaWorker.class).setInputData(data).build();
                        Core.getWorkManager().enqueue(uploadRequest);
                        ReUsableFunctions.ShowToast("Uploading");
                        requireActivity().finish();

                    }else{
                        data=new Data.Builder()
                                .putString("type","video")
                                .putString("path",file.getAbsolutePath())
                                .putString("caption",caption)
                                .build();
                        uploadRequest=new OneTimeWorkRequest.Builder(UploadMediaWorker.class).setInputData(data).build();
                        Core.getWorkManager().enqueue(uploadRequest);
                        ReUsableFunctions.ShowToast("Uploading");
                        requireActivity().finish();

                    }


                }else {
                    ReUsableFunctions.ShowToast(activity,"no image found err");
                    requireActivity().onBackPressed();
                }
            }
        });
    }

}