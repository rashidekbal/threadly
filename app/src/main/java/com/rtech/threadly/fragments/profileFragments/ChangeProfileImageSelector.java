package com.rtech.threadly.fragments.profileFragments;

import android.Manifest;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtech.threadly.R;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.adapters.uploadProfileAdapter;
import com.rtech.threadly.databinding.FragmentChangeProfileImageSelectorBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.uploadProfileSelectCallbackInterface;
import com.rtech.threadly.managers.ProfileEditorManager;
import com.rtech.threadly.utils.PermissionManagementUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChangeProfileImageSelector extends Fragment {
    FragmentChangeProfileImageSelectorBinding mainXml;
    String AndroidAPi33AndAbovePermission= Manifest.permission.READ_MEDIA_IMAGES;
    String AndroidApi32AndBelowPermission=Manifest.permission.READ_EXTERNAL_STORAGE;
    ArrayList<Uri> uris;
    Uri pickedUri;
    ProfileEditorManager profileEditorManager;
    ProfileViewModel profileViewModel;



    public ChangeProfileImageSelector() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       mainXml=FragmentChangeProfileImageSelectorBinding.inflate(inflater,container,false);
       profileViewModel=new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
       profileEditorManager=new ProfileEditorManager();
       checkPermissionAndStart();
        return mainXml.getRoot();
    }

    private void checkPermissionAndStart() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           if( !PermissionManagementUtil.isAllPermissionGranted(requireContext(),new String []{AndroidAPi33AndAbovePermission})){
               PermissionManagementUtil.requestPermission(requireActivity(),new String[]{AndroidAPi33AndAbovePermission},101);
           }else{
               StartMain();
           }

        }else{
          if(!PermissionManagementUtil.isAllPermissionGranted(requireContext(),new String[]{AndroidApi32AndBelowPermission})){
              PermissionManagementUtil.requestPermission(requireActivity(),new String[]{AndroidApi32AndBelowPermission},101);
          }else{
              StartMain();
          }
        }

    }

    private void StartMain() {
        pickedUri=null;
       uris =new ArrayList<>();
        uploadProfileAdapter adapter=new uploadProfileAdapter(uris, requireContext(), new uploadProfileSelectCallbackInterface() {
            @Override
            public void itemPicked(Uri uri) {
                mainXml.selectImagePreview.setImageURI(uri);
                pickedUri=uri;
            }
        });
        GridLayoutManager layoutManager=new GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false);
        mainXml.mediaRecyclerView.setLayoutManager(layoutManager);
        mainXml.mediaRecyclerView.setAdapter(adapter);



        Uri collection = MediaStore.Files.getContentUri("external");
        String [] projection={
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String  selection=
                MediaStore.Files.FileColumns.MEDIA_TYPE+" = "+MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

        String SortOrder= MediaStore.Files.FileColumns.DATE_ADDED+" DESC";

        Cursor cursor=requireContext().getContentResolver().query(collection,projection,selection,null,SortOrder);
        if(cursor!=null){
            int idColumn =cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            while(cursor.moveToNext()){
                Uri uri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cursor.getLong(idColumn));
                uris.add(uri);
            }
            if(!uris.isEmpty()){
            mainXml.selectImagePreview.setImageURI(uris.get(0));
            pickedUri=uris.get(0);}
        }
        setOnclickListeners();

    }

    private void setOnclickListeners() {
        mainXml.cancelButton.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });
        mainXml.nextBtn.setOnClickListener(v->{
            mainXml.nextBtn.setEnabled(false);
            mainXml.progressbar.setVisibility(View.VISIBLE);
          if(pickedUri!=null){
              // upload logic
              try {
                  File photo=ReUsableFunctions.getFileFromUri(Threadly.getGlobalContext(),pickedUri);
                  profileEditorManager.ChangeUserProfile(photo, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                      @Override
                      public void onSuccess(JSONObject response) {
                          JSONObject data= null;
                          try {
                              data = response.getJSONObject("data");
                              String url=data.getString("url");
                              ReUsableFunctions.ShowToast("profile pic uploaded");
                              profileEditorManager.updateUserProfile(url);
                              profileViewModel.loadProfile();
                              photo.delete();
                              mainXml.progressbar.setVisibility(View.GONE);
                              requireActivity().onBackPressed();

                          } catch (JSONException e) {
                              ReUsableFunctions.ShowToast("error uploading profile pic");
                              mainXml.nextBtn.setEnabled(true);
                              mainXml.progressbar.setVisibility(View.GONE);
                              throw new RuntimeException(e);
                          }


                      }

                      @Override
                      public void onError(String err) {
                          ReUsableFunctions.ShowToast("error uploading profile pic");
                          mainXml.nextBtn.setEnabled(true);
                          mainXml.progressbar.setVisibility(View.GONE);

                      }
                  });
              } catch (IOException e) {
                  ReUsableFunctions.ShowToast("error uploading profile pic");
                  mainXml.nextBtn.setEnabled(true);
                  mainXml.progressbar.setVisibility(View.GONE);
                  throw new RuntimeException(e);

              }


          }else{
              mainXml.progressbar.setVisibility(View.GONE);
              ReUsableFunctions.ShowToast("please select an image ");
              mainXml.nextBtn.setEnabled(true);

          }
        });
    }
}