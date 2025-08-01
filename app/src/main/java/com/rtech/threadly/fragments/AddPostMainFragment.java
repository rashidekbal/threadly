package com.rtech.threadly.fragments;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rtech.threadly.adapters.mediaExplorerAdapter.AddPostShowMediaAdapter;
import com.rtech.threadly.databinding.FragmentAddPostMainBinding;
import com.rtech.threadly.interfaces.AddPostMainFragmentOptionsClickInterface;
import com.rtech.threadly.models.MediaModel;
import com.rtech.threadly.utils.ReUsableFunctions;

import java.util.ArrayList;


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
                setMediaType(type);
                setSelectedImageUri(Uri.parse(uri));
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
    private void setMediaType(String type){
        MediaType=type;
    }
    private  void setSelectedImageUri(Uri uri){
        selectedImageUri=uri;

    }


}