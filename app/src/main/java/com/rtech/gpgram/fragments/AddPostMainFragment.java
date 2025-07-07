package com.rtech.gpgram.fragments;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY;
import static androidx.camera.core.ImageCapture.FLASH_MODE_OFF;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;
import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.AddPostShowMediaAdapter;
import com.rtech.gpgram.databinding.FragmentAddPostMainBinding;
import com.rtech.gpgram.interfaces.AddPostMainFragmentOptionsClickInterface;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.managers.PostsManager;
import com.rtech.gpgram.models.MediaModel;
import com.rtech.gpgram.utils.ReUsableFunctions;
import com.rtech.gpgram.viewmodels.ProfileViewModel;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class AddPostMainFragment extends Fragment {
    FragmentAddPostMainBinding mainXml;
    AppCompatActivity activity;
    AddPostMainFragmentOptionsClickInterface callback;
   ArrayList <MediaModel> MediaList;
    AddPostShowMediaAdapter adapter;
    Uri selectedImageUri;
    String MediaType;



    public AddPostMainFragment(AddPostMainFragmentOptionsClickInterface callback) {
        this.callback=callback;

        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainXml=FragmentAddPostMainBinding.inflate(inflater,container,false);

        StartMainFunction();


        return mainXml.getRoot();






    }

    private void StartMainFunction() {
     activity = (AppCompatActivity) getActivity();
     MediaList=new ArrayList<>();
     MediaList.add(new MediaModel(Uri.fromParts("package",activity.getPackageName(),null),false,0,true));
        GridLayoutManager layoutManager=new GridLayoutManager(activity,4,GridLayoutManager.VERTICAL,false);
        adapter=new AddPostShowMediaAdapter(activity, MediaList, new AddPostMainFragmentOptionsClickInterface() {
            @Override
            public void openCamera() {
                callback.openCamera();
            }

            @Override
            public void itemPicked(String uri,String type) {
                MediaType=type;
                selectedImageUri=Uri.parse(uri);
                Glide.with(activity).load(Uri.parse(uri)).into(mainXml.selectImagePreview);



            }
        });
        mainXml.mediaRecyclerView.setLayoutManager(layoutManager);
        mainXml.mediaRecyclerView.setAdapter(adapter);
        mainXml.mediaRecyclerView.setNestedScrollingEnabled(true);
        getInternalImage_video();
        setOnclickListeners();


    }

    private void setOnclickListeners(){
        mainXml.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        mainXml.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImageUri!=null){
                    callback.itemPicked(selectedImageUri.toString(),MediaType);
                }else{
                    ReUsableFunctions.ShowToast(activity,"Please select an image or video to continue");
                }

            }
        });

    }

    private void getInternalImage_video(){
        Uri collection = MediaStore.Files.getContentUri("external");
        String [] projection={
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.DURATION,
                MediaStore.Files.FileColumns.DATE_ADDED
        };
        String selection=MediaStore.Files.FileColumns.MEDIA_TYPE+"="+MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE+" OR "+
                MediaStore.Files.FileColumns.MEDIA_TYPE+"="+
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        String SortOrder=MediaStore.Files.FileColumns.DATE_ADDED+" DESC";

        Cursor cursor= getContext().getContentResolver().query(collection,projection,selection,null,SortOrder);
        if(cursor!=null){
            int idColumn =cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int mediaTypeColumn=cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int duration =cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION);
            while(cursor.moveToNext()){
                long id=cursor.getLong(idColumn);
                int mediaType=cursor.getInt(mediaTypeColumn);
                int durationVideo=Math.round((mediaType==MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO?cursor.getFloat(duration):0)/1000);
                Log.d("duration", "getInternalImage_video: "+Float.toString(durationVideo));
                Uri uri=(mediaType==MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)?
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)
                        :ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id);
                MediaList.add(new MediaModel(uri,mediaType==MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,mediaType==MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO?durationVideo:0,false));
            }
            if(MediaList.size()>1){
                mainXml.selectImagePreview.setImageURI(MediaList.get(1).uri);
                selectedImageUri= MediaList.get(1).uri;
                if(MediaList.get(1).isVideo){
                    MediaType="video";}else{
                    MediaType="image";
                }
            }

            cursor.close();
            adapter.notifyDataSetChanged();

        }
    }


}